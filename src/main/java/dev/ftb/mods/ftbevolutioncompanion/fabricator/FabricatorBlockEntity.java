package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.ftb.mods.ftbevolutioncompanion.CompanionSounds;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

public final class FabricatorBlockEntity extends BlockEntity implements MenuProvider, GeoBlockEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.ftb_fabricator.idle");
    private static final RawAnimation WORKING = RawAnimation.begin().thenLoop("animation.ftb_fabricator.working");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack displayItem = ItemStack.EMPTY;
    public static final int INPUTS = 9;
    public static final int OUTPUTS = 3;
    public static final int TANK_CAPACITY = 16000;
    public static final int ENERGY_CAPACITY = 1000000000;

    public enum Status {
        IDLE,
        WORKING,
        STAGE_REQUIRED,
        NO_POWER,
        OUTPUT_FULL,
        NO_OWNER
    }

    private final ItemStacksResourceHandler items = new ItemStacksResourceHandler(INPUTS + OUTPUTS) {
        @Override
        protected void onContentsChanged(int index, ItemStack previous) {
            dirty = true;
            syncPending = true;
            setChanged();
        }
    };
    private final FluidStacksResourceHandler fluids = new FluidStacksResourceHandler(3, TANK_CAPACITY) {
        @Override
        protected void onContentsChanged(int index, FluidStack previous) {
            dirty = true;
            syncPending = true;
            setChanged();
        }
    };
    private final SimpleEnergyHandler energy = new SimpleEnergyHandler(ENERGY_CAPACITY, ENERGY_CAPACITY, 0) {
        @Override
        protected void onEnergyChanged(int previous) {
            syncPending = true;
            setChanged();
        }
    };
    private final ResourceHandler<ItemResource> automationItems = new PortHandler<>(items, INPUTS);
    private final ResourceHandler<FluidResource> automationFluids = new PortHandler<>(fluids, 2);
    private @Nullable UUID owner;
    private String ownerName = "";
    private Set<String> ownerTags = new HashSet<>();
    private Status status = Status.IDLE;
    private String requiredStage = "";
    private String activeId = "";
    private String recipeSignature = "";
    private int progress;
    private int duration;
    private int powerUsage;
    private boolean dirty = true;
    private boolean syncPending;
    private @Nullable RecipeHolder<FabricatorRecipe> selected;
    private List<RecipeHolder<FabricatorRecipe>> recipes = List.of();
    private Object recipeMap;

    public FabricatorBlockEntity(BlockPos pos, BlockState state) {
        super(FabricatorRegistry.BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStacksResourceHandler items() {
        return items;
    }

    public FluidStacksResourceHandler fluids() {
        return fluids;
    }

    public boolean clearInputTank(int tank) {
        if (tank < 0 || tank >= 2 || fluids.getAmountAsInt(tank) <= 0) return false;
        try (Transaction transaction = Transaction.openRoot()) {
            int drained = fluids.extract(tank, fluids.getResource(tank), fluids.getAmountAsInt(tank), transaction);
            if (drained <= 0) return false;
            transaction.commit();
        }
        return true;
    }

    public SimpleEnergyHandler energy() {
        return energy;
    }

    public ResourceHandler<ItemResource> automationItems() {
        return automationItems;
    }

    public ResourceHandler<FluidResource> automationFluids() {
        return automationFluids;
    }

    public Status status() {
        return status;
    }

    public String requiredStage() {
        return requiredStage;
    }

    public String ownerName() {
        return ownerName;
    }

    public int progress() {
        return progress;
    }

    public int duration() {
        return duration;
    }

    public int powerUsage() {
        return powerUsage;
    }

    public ItemStack displayItem() {
        return displayItem;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<FabricatorBlockEntity>(
                "main", 5, test -> test.setAndContinue(test.animatable().status() == Status.WORKING ? WORKING : IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public boolean canConnect(@Nullable Direction side) {
        return side != null && side != getBlockState().getValue(FabricatorBlock.FACING);
    }

    @Override
    public void setBlockState(BlockState state) {
        Direction oldFacing = getBlockState().getValue(FabricatorBlock.FACING);
        super.setBlockState(state);
        if (level != null && oldFacing != state.getValue(FabricatorBlock.FACING))
            level.invalidateCapabilities(worldPosition);
    }

    public void setOwner(ServerPlayer player) {
        owner = player.getUUID();
        ownerName = player.getGameProfile().name();
        ownerTags = new HashSet<>(player.entityTags());
        dirty = true;
        sync();
    }

    private void refreshOwner(ServerLevel server) {
        if (owner == null) return;
        ServerPlayer player = server.getServer().getPlayerList().getPlayer(owner);
        if (player != null && !ownerTags.equals(player.entityTags())) {
            ownerTags = new HashSet<>(player.entityTags());
            dirty = true;
            setChanged();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FabricatorBlockEntity machine) {
        if (level instanceof ServerLevel server) machine.serverTick(server);
    }

    private void serverTick(ServerLevel server) {
        refreshOwner(server);
        Object currentMap = server.recipeAccess().recipeMap();
        if (recipeMap != currentMap) {
            recipeMap = currentMap;
            recipes = server.recipeAccess().recipeMap().byType(FabricatorRegistry.RECIPE_TYPE.get()).stream()
                    .sorted(Comparator.comparing(
                            holder -> holder.id().identifier().toString()))
                    .toList();
            dirty = true;
        }
        Status previousStatus = status;
        if (dirty) selectRecipe(server);
        if (selected != null) process(server);
        boolean working = status == Status.WORKING;
        if (getBlockState().getValue(FabricatorBlock.WORKING) != working) {
            server.setBlock(
                    worldPosition, getBlockState().setValue(FabricatorBlock.WORKING, working), Block.UPDATE_CLIENTS);
        }
        if (previousStatus != status || syncPending && server.getGameTime() % 10 == 0) sync();
        if (working && server.getGameTime() % 60 == 0) {
            server.playSound(null, worldPosition, CompanionSounds.FABRICATOR_WORKING.get(), SoundSource.BLOCKS, 1F, 1F);
        }
    }

    private FabricatorInput input() {
        return new FabricatorInput(
                items.copyToList().subList(0, INPUTS), fluids.copyToList().subList(0, 2));
    }

    private void selectRecipe(ServerLevel server) {
        dirty = false;
        selected = null;
        displayItem = ItemStack.EMPTY;
        requiredStage = "";
        status = Status.IDLE;
        FabricatorInput input = input();
        for (RecipeHolder<FabricatorRecipe> holder : recipes) {
            FabricatorRecipe recipe = holder.value();
            if (!recipe.matches(input, server)) continue;
            if (!recipe.stage().isEmpty() && (owner == null || !ownerTags.contains(recipe.stage()))) {
                if (requiredStage.isEmpty()) requiredStage = recipe.stage();
                status = owner == null ? Status.NO_OWNER : Status.STAGE_REQUIRED;
                continue;
            }
            selected = holder;
            displayItem = recipe.results().isEmpty()
                    ? ItemStack.EMPTY
                    : recipe.results().getFirst().withCount(1).create();
            requiredStage = recipe.stage();
            String id = holder.id().identifier().toString();
            String signature = FabricatorRecipe.CODEC
                    .codec()
                    .encodeStart(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), recipe)
                    .result()
                    .map(Object::toString)
                    .orElse("");
            if (!id.equals(activeId) || !signature.equals(recipeSignature)) progress = 0;
            activeId = id;
            recipeSignature = signature;
            duration = recipe.ticks();
            powerUsage = recipe.energyPerTick();
            return;
        }
        progress = 0;
        duration = 0;
        powerUsage = 0;
        activeId = "";
        recipeSignature = "";
    }

    private void process(ServerLevel server) {
        FabricatorRecipe recipe = selected.value();
        try (Transaction simulation = Transaction.openRoot()) {
            if (!insertResults(recipe, simulation)) {
                status = Status.OUTPUT_FULL;
                return;
            }
        }
        if (energy.getAmountAsLong() < recipe.energyPerTick()) {
            status = Status.NO_POWER;
            return;
        }
        if (progress + 1 >= recipe.ticks()) {
            FabricatorInput input = input();
            int[] consumedItems = recipe.itemAllocation(input);
            int[] consumedFluids = recipe.fluidAllocation(input);
            if (consumedItems == null || consumedFluids == null) {
                dirty = true;
                progress = 0;
                return;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                if (!insertResults(recipe, transaction)) {
                    status = Status.OUTPUT_FULL;
                    return;
                }
                for (int i = 0; i < INPUTS; i++) {
                    if (consumedItems[i] > 0
                            && items.extract(i, items.getResource(i), consumedItems[i], transaction)
                                    != consumedItems[i]) return;
                }
                for (int i = 0; i < 2; i++) {
                    if (consumedFluids[i] > 0
                            && fluids.extract(i, fluids.getResource(i), consumedFluids[i], transaction)
                                    != consumedFluids[i]) return;
                }
                transaction.commit();
            }
            progress = 0;
            server.playSound(
                    null, worldPosition, CompanionSounds.FABRICATOR_COMPLETE.get(), SoundSource.BLOCKS, 1F, 1F);
        } else {
            progress++;
        }
        energy.set(energy.getAmountAsInt() - recipe.energyPerTick());
        status = Status.WORKING;
        syncPending = true;
        setChanged();
    }

    private boolean insertResults(FabricatorRecipe recipe, TransactionContext transaction) {
        for (var template : recipe.results()) {
            ItemStack stack = template.create();
            if (stack.isEmpty()) return false;
            int remaining = stack.getCount();
            ItemResource resource = ItemResource.of(stack);
            for (int i = INPUTS; i < INPUTS + OUTPUTS && remaining > 0; i++)
                remaining -= items.insert(i, resource, remaining, transaction);
            if (remaining > 0) return false;
        }
        for (var template : recipe.fluidResults()) {
            FluidStack stack = template.create();
            if (fluids.insert(2, FluidResource.of(stack), stack.getAmount(), transaction) != stack.getAmount())
                return false;
        }
        return true;
    }

    private void sync() {
        syncPending = false;
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ftbevolutioncompanion.ftb_fabricator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FabricatorMenu(id, inventory, this);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {}

    public boolean hasContents() {
        return items.copyToList().stream().anyMatch(stack -> !stack.isEmpty())
                || fluids.copyToList().stream().anyMatch(stack -> !stack.isEmpty())
                || energy.getAmountAsInt() > 0;
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        NonNullList<ItemStack> stacks = NonNullList.withSize(INPUTS + OUTPUTS, ItemStack.EMPTY);
        components
                .getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
                .copyInto(stacks);
        for (int i = 0; i < stacks.size(); i++)
            items.set(i, ItemResource.of(stacks.get(i)), stacks.get(i).getCount());
        List<SimpleFluidContent> tanks = components.getOrDefault(FabricatorRegistry.FLUIDS.get(), List.of());
        for (int i = 0; i < Math.min(tanks.size(), fluids.size()); i++) {
            FluidStack stack = tanks.get(i).copy();
            fluids.set(i, FluidResource.of(stack), stack.getAmount());
        }
        energy.set(Math.clamp(components.getOrDefault(FabricatorRegistry.ENERGY.get(), 0), 0, ENERGY_CAPACITY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        NonNullList<ItemStack> stacks = items.copyToList();
        if (stacks.stream().anyMatch(stack -> !stack.isEmpty()))
            components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
        NonNullList<FluidStack> tanks = fluids.copyToList();
        if (tanks.stream().anyMatch(stack -> !stack.isEmpty()))
            components.set(
                    FabricatorRegistry.FLUIDS.get(),
                    tanks.stream().map(SimpleFluidContent::copyOf).toList());
        if (energy.getAmountAsInt() > 0) components.set(FabricatorRegistry.ENERGY.get(), energy.getAmountAsInt());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("items");
        output.discard("fluids");
        output.discard("energy");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        items.serialize(output.child("items"));
        fluids.serialize(output.child("fluids"));
        energy.serialize(output.child("energy"));
        if (owner != null) output.putString("owner", owner.toString());
        output.putString("owner_name", ownerName);
        output.store(
                "owner_tags", Codec.STRING.listOf(), ownerTags.stream().sorted().toList());
        output.putInt("progress", progress);
        output.putInt("duration", duration);
        output.putInt("power_usage", powerUsage);
        output.putString("active_recipe", activeId);
        output.putString("recipe_signature", recipeSignature);
        output.putString("required_stage", requiredStage);
        output.putInt("status", status.ordinal());
        output.store("display_item", ItemStack.OPTIONAL_CODEC, displayItem);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("items").ifPresent(items::deserialize);
        input.child("fluids").ifPresent(fluids::deserialize);
        input.child("energy").ifPresent(energy::deserialize);
        String ownerId = input.getStringOr("owner", "");
        try {
            owner = ownerId.isEmpty() ? null : UUID.fromString(ownerId);
        } catch (IllegalArgumentException ignored) {
            owner = null;
        }
        ownerName = input.getStringOr("owner_name", "");
        ownerTags =
                new HashSet<>(input.read("owner_tags", Codec.STRING.listOf()).orElse(List.of()));
        duration = Math.clamp(input.getIntOr("duration", 0), 0, 72000);
        progress = Math.clamp(input.getIntOr("progress", 0), 0, Math.max(0, duration - 1));
        powerUsage = Math.clamp(input.getIntOr("power_usage", 0), 0, ENERGY_CAPACITY);
        activeId = input.getStringOr("active_recipe", "");
        recipeSignature = input.getStringOr("recipe_signature", "");
        requiredStage = input.getStringOr("required_stage", "");
        status = Status.values()[Math.clamp(input.getIntOr("status", 0), 0, Status.values().length - 1)];
        displayItem = input.read("display_item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        dirty = true;
    }

    private static final class PortHandler<T extends Resource> extends DelegatingResourceHandler<T> {
        private final int inputs;

        private PortHandler(ResourceHandler<T> delegate, int inputs) {
            super(delegate);
            this.inputs = inputs;
        }

        @Override
        public boolean isValid(int index, T resource) {
            return index < inputs && super.isValid(index, resource);
        }

        @Override
        public int insert(int index, T resource, int amount, TransactionContext transaction) {
            return index < inputs ? super.insert(index, resource, amount, transaction) : 0;
        }

        @Override
        public int extract(int index, T resource, int amount, TransactionContext transaction) {
            return index >= inputs ? super.extract(index, resource, amount, transaction) : 0;
        }
    }
}
