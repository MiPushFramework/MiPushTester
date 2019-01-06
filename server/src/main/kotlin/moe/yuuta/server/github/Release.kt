package moe.yuuta.server.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The release bean of GitHub API
 * NOTICE: It does not contain all attributes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Release(
        @JsonProperty("url") var url: String = "",
        @JsonProperty("html_url") var htmlUrl: String = "",
        @JsonProperty("id") var id: Int = -1,
        @JsonProperty("name") var name: String = "",
        @JsonProperty("body") var body: String = "",
        @JsonProperty("tag_name") var tagName: String = ""
)