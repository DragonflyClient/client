package net.inceptioncloud.dragonfly.utils

import net.minecraft.client.gui.GuiScreen
import java.net.URI

/**
 * A simple utility class to generate links for Google Analytics Campaigns.
 *
 * @param baseUrl The base url of the website without `https://`
 */
class CampaignLink(private val baseUrl: String) {

    /** The `utm_source` parameter */
    private var source: String? = null

    /** The `utm_medium` parameter */
    private var medium: String? = null

    /** The `utm_campaign` parameter */
    private var campaign: String? = null

    companion object {
        /**
         * Creates a campaign link from this string
         */
        fun String.link() = CampaignLink(this)
    }

    /**
     * Builds the full url of the campaign link
     */
    fun build(): String {
        val fullUrl = "https://$baseUrl"
        val params = buildList {
            if (source != null) add("utm_source=$source")
            if (medium != null) add("utm_medium=$medium")
            if (campaign != null) add("utm_campaign=$campaign")
        }

        if (params.isEmpty()) return fullUrl
        return "$fullUrl?" + params.joinToString("&")
    }

    /**
     * Opens the link using the default web browser
     */
    fun open() = GuiScreen.openWebLink(URI(build()))

    /** Sets the [source] attribute */
    fun source(source: String) = apply { this.source = source }

    /** Sets the [medium] attribute */
    fun medium(medium: String) = apply { this.medium = medium }

    /** Sets the [campaign] attribute */
    fun campaign(campaign: String) = apply { this.campaign = campaign }
}