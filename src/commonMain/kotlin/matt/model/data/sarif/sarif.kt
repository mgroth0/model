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
        if (other.issueCount == 0u) return this
        if (issueCount == 0u) return other
        if (other.issueCount == 0u && issueCount == 0u) return this
        val allRuns = (runs + other.runs).toList()
        if (allRuns.isNotEmpty()) {
            val firstRun = allRuns.first()
            val firstTool = firstRun.tool
            check(allRuns.all { it.tool == firstTool })
            val mergedRun = firstRun.copy(
                results = allRuns.flatMapTo(mutableSetOf()) { it.results }
            )
            return copy(runs = setOf(mergedRun))
        }
        return copy(runs = allRuns.toSet())
    }

}


@Serializable
data class AnalysisRun(
    val results: Set<AnalysisResult> /*idk*/,
    val tool: AnalysisTool
) {
    val issueCount by lazy {
        results.sumOf { it.issueCount }
    }
}


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
    val uri: String
)

@Serializable
data class PhysicalRegion(
    val endColumn: Int,
    val endLine: Int,
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
    val guid: String,
    val informationUri: String,
    val language: String,
    val name: String,
    val organization: String,
    val rules: Set<AnalysisRule>,
    val semanticVersion: String,
    val version: String
)

@Serializable
data class AnalysisRule(
    val helpUri: String,
    val id: String,
    val name: String,
    val shortDescription: Description
)


@Serializable
data class Description(
    val text: String
)