package de.lucalabs.delivery;

import de.lucalabs.delivery.entities.SddEntities;
import de.lucalabs.delivery.renderer.PlacedShippingLabelRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SameDayDeliveryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(SddEntities.SHIPPING_LABEL, PlacedShippingLabelRenderer::new);
	}
}