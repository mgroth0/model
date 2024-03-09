package matt.model.data.sarif

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Sarif(
    @SerialName("\$schema")
    val schema: String,
    val version: String,
    val runs: Set<AnalysisRun>
) {
    val issueCount by lazy {
        runs.sumOf { it.issueCount }
    }

    operator fun plus(other: Sarif): Sarif {
        check(other.schema == schema)
        check(other.version == version)
        return copy(
            runs =
                (runs + other.runs).groupBy {
                    it.mergeGroup
                }.map {
                    it.value.reduce { acc, analysisRun -> acc + analysisRun }
                }.toSet()
        )
    }
}


@Serializable
data class AnalysisRun(
    val results: Set<AnalysisResult> /*idk*/,
    val tool: AnalysisTool,
    val originalUriBaseIds: UriBaseIds? = null
) {
    val issueCount by lazy {
        results.sumOf { it.issueCount }
    }
    val mergeGroup: AnalysisTool get() = tool
    fun canMergeWith(run: AnalysisRun) = tool == run.tool
    operator fun plus(run: AnalysisRun): AnalysisRun {
        require(canMergeWith(run))
        return copy(results = results + run.results)
    }
}


@Serializable
data class UriBaseIds(
    @Suppress("SpellCheckingInspection")
    @SerialName("%SRCROOT%")
    val srcRoot: SrcRoot
)

@Serializable
data class SrcRoot(
    val uri: String
)

@Serializable
data class AnalysisResult(
    val level: String,
    val locations: Set<CodeLocation>,
    val message: Description,
    val ruleId: String
) {
    val issueCount: UInt by lazy {
        check(locations.isNotEmpty())
        locations.size.toUInt()
    }
}

@Serializable
data class CodeLocation(
    val physicalLocation: PhysicalLocation
)

@Serializable
data class PhysicalLocation(
    val artifactLocation: ArtifactLocation,
    val region: PhysicalRegion
)

@Serializable
data class ArtifactLocation(
    val uri: String,
    val uriBaseId: String? = null
)

@Serializable
data class PhysicalRegion(
    val endColumn: Int? = null,
    val endLine: Int? = null,
    val startColumn: Int,
    val startLine: Int
)

@Serializable
data class AnalysisTool(
    val driver: AnalysisDriver
)

@Serializable
data class AnalysisDriver(
    val downloadUri: String,
    val fullName: String,
    val guid: String? = null,
    val informationUri: String,
    val language: String,
    val name: String,
    val organization: String,
    val rules: Set<AnalysisRule>,
    val semanticVersion: String? = null,
    val version: String? = null
)


@Serializable
data class AnalysisRule(
    val helpUri: String,
    val id: String,
    val name: String,
    val shortDescription: Description,
    val defaultConfiguration: DefaultConfiguration? = null
)

@Serializable
data class DefaultConfiguration(
    val level: String
)

@Serializable
data class Description(
    val text: String
)



