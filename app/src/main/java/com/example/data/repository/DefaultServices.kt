package com.example.data.repository

import com.example.data.model.SmmPlatform
import com.example.data.model.SmmService

object DefaultServices {
    fun getDefaultServices(usdToBdt: Double = 125.0, marginFactor: Double = 1.30): List<SmmService> {
        val rawList = listOf(
            // Facebook
            RawService(
                101, "Facebook Profile & Page Followers [High Quality - Non Drop - 30 Days Refill]",
                "Facebook Followers", SmmPlatform.FACEBOOK, 0.95, 50, 100000,
                "High quality real followers. Speed: 5K-10K/Day. Guaranteed non-drop with 30 days refill."
            ),
            RawService(
                102, "Facebook Post Reactions (Like / Love / Care / Wow) [Instant]",
                "Facebook Reactions", SmmPlatform.FACEBOOK, 0.40, 20, 50000,
                "Instant start within 2 minutes. Safe for personal and business pages."
            ),
            RawService(
                103, "Facebook 60,000 Minutes Watch Time for In-Stream Monetization",
                "Facebook Monetization", SmmPlatform.FACEBOOK, 7.50, 1, 10,
                "Essential for Facebook ad breaks monetization. 100% organic watch retention."
            ),
            RawService(
                104, "Facebook Live Stream Viewers (60 Minutes) [Instant]",
                "Facebook Live", SmmPlatform.FACEBOOK, 1.80, 10, 5000,
                "Boost your live audience immediately. Increases live ranking and engagement."
            ),
            RawService(
                105, "Facebook Video & Reels Views [Ultra Fast - 1M/Day]",
                "Facebook Views", SmmPlatform.FACEBOOK, 0.15, 100, 1000000,
                "Super fast reel views. Great for viral algorithm push."
            ),

            // YouTube
            RawService(
                201, "YouTube Non-Drop Subscribers [Real Accounts - Lifetime Guarantee]",
                "YouTube Subscribers", SmmPlatform.YOUTUBE, 6.20, 50, 20000,
                "Real worldwide subscribers with natural delivery. Lifetime refill guarantee."
            ),
            RawService(
                202, "YouTube 4000 Hours Watch Time Pack [Monetization Safe]",
                "YouTube Monetization", SmmPlatform.YOUTUBE, 18.00, 1, 10,
                "Full watch time package for channel monetization approval. Video length must be 15+ minutes."
            ),
            RawService(
                203, "YouTube High Retention Views [Suggested Videos + Browse Features]",
                "YouTube Views", SmmPlatform.YOUTUBE, 1.40, 500, 500000,
                "Retention 2-5 minutes per view. Safe for Adsense monetization."
            ),
            RawService(
                204, "YouTube Shorts Views [Instant Viral Push]",
                "YouTube Shorts", SmmPlatform.YOUTUBE, 0.35, 500, 1000000,
                "Start time: 0-10 min. Boosts your Shorts to trending."
            ),
            RawService(
                205, "YouTube Custom Bangladeshi Comments [Bangla / English]",
                "YouTube Engagement", SmmPlatform.YOUTUBE, 12.00, 10, 1000,
                "Custom Bengali/English comments specified by you. Improves trust."
            ),

            // TikTok
            RawService(
                301, "TikTok Followers [Real Look - Fast Speed - 30D Refill]",
                "TikTok Followers", SmmPlatform.TIKTOK, 1.60, 50, 100000,
                "Organic-looking profiles with avatars. Speed: 10K-25K/day."
            ),
            RawService(
                302, "TikTok Video Views [Ultra Fast - 10M Limit]",
                "TikTok Views", SmmPlatform.TIKTOK, 0.05, 500, 10000000,
                "Instant start. Extremely cheap and effective for TikTok FYP algorithm."
            ),
            RawService(
                303, "TikTok Hearts / Likes [High Quality]",
                "TikTok Likes", SmmPlatform.TIKTOK, 0.70, 50, 50000,
                "Stable likes from active-looking accounts. Instant delivery."
            ),
            RawService(
                304, "TikTok Shares + Saves (Combo)",
                "TikTok Growth", SmmPlatform.TIKTOK, 0.30, 50, 100000,
                "Triggers TikTok recommendation algorithm to boost video to more viewers."
            ),

            // Instagram
            RawService(
                401, "Instagram Followers [Guaranteed Refill 365 Days]",
                "Instagram Followers", SmmPlatform.INSTAGRAM, 1.20, 50, 500000,
                "Stable non-drop followers with 365 days auto-refill button."
            ),
            RawService(
                402, "Instagram High Quality Likes [Instant Start - Real]",
                "Instagram Likes", SmmPlatform.INSTAGRAM, 0.30, 50, 100000,
                "Instant start within 60 seconds. High quality profiles."
            ),
            RawService(
                403, "Instagram Reels Views + Reach + Impressions [Combo]",
                "Instagram Reels", SmmPlatform.INSTAGRAM, 0.20, 100, 1000000,
                "Boost your reel directly to Explore tab with reach + impressions."
            ),
            RawService(
                404, "Instagram Story Views [All Stories Active]",
                "Instagram Stories", SmmPlatform.INSTAGRAM, 0.45, 100, 50000,
                "Views all active stories on the account."
            ),

            // Telegram
            RawService(
                501, "Telegram Channel / Group Members [0% Drop - Permanent]",
                "Telegram Members", SmmPlatform.TELEGRAM, 1.10, 50, 50000,
                "Safe for channels and supergroups. No drop, 0-1 hour start."
            ),
            RawService(
                502, "Telegram Post Views (Last 5 Posts Auto Views)",
                "Telegram Views", SmmPlatform.TELEGRAM, 0.25, 100, 100000,
                "Delivers views to recent posts to make channel look active."
            ),

            // Twitter / X
            RawService(
                601, "Twitter (X) Followers [Real Profiles with Bio & Posts]",
                "Twitter Followers", SmmPlatform.TWITTER, 2.50, 50, 20000,
                "High quality Twitter accounts. Safe and non-drop."
            ),
            RawService(
                602, "Twitter (X) Retweets & Likes Combo",
                "Twitter Engagement", SmmPlatform.TWITTER, 1.80, 50, 25000,
                "Dual action boost for tweets."
            )
        )

        return rawList.map { raw ->
            val bdtRate = Math.round(raw.rateUsd * usdToBdt * marginFactor * 10.0) / 10.0
            SmmService(
                serviceId = raw.id,
                name = raw.name,
                category = raw.category,
                platform = raw.platform,
                rateUsd = raw.rateUsd,
                rateBdt = bdtRate,
                min = raw.min,
                max = raw.max,
                description = raw.desc
            )
        }
    }

    private data class RawService(
        val id: Int,
        val name: String,
        val category: String,
        val platform: SmmPlatform,
        val rateUsd: Double,
        val min: Int,
        val max: Int,
        val desc: String
    )
}
