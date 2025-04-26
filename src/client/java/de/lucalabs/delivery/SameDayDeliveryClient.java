package de.lucalabs.delivery;

import de.lucalabs.delivery.entities.PlacedShippingLabel;
import de.lucalabs.delivery.entities.SddEntities;
import de.lucalabs.delivery.renderer.PlacedShippingLabelRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import java.util.ArrayList;
import java.util.List;

public class SameDayDeliveryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(SddEntities.SHIPPING_LABEL, PlacedShippingLabelRenderer::new);

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(PlacedShippingLabelRenderer.MODEL);
		});
	}
}