import sbt._
import Keys._
import PlayProject._

object Plugin extends Build {

  val pluginName = "play-scalr"
  val pluginVersion = "0.1-SNAPSHOT"

  val pluginDependencies = Seq(
    "org.imgscalr" % "imgscalr-lib" % "4.2",
    "commons-io" % "commons-io" % "2.4",
    "se.digiplant" %% "play-res" % "0.1-SNAPSHOT"
  )

  lazy val res = PlayProject(pluginName, pluginVersion, pluginDependencies, mainLang = SCALA, settings = Defaults.defaultSettings ++ Publish.settings ++ Ls.settings)
    .settings(
      organization := "se.digiplant",
      playPlugin := true,
      shellPrompt := ShellPrompt.buildShellPrompt,

      resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    )
}

object Publish {
  lazy val settings = Seq(
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/digiPlant/play-scalr")),
    pomExtra := (
      <scm>
        <url>git://github.com/digiPlant/play-scalr.git</url>
        <connection>scm:git://github.com/digiPlant/play-scalr.git</connection>
      </scm>
        <developers>
          <developer>
            <id>leon</id>
            <name>Leon Radley</name>
            <url>http://github.com/leon</url>
          </developer>
        </developers>)
  )
}

// Shell prompt which show the current project, git branch and build version
object ShellPrompt {
  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) { }
    def buffer[T] (f: => T): T = f
  }
  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## "
    )

  val buildShellPrompt = {
    (state: State) => {
      val currProject = Project.extract (state).currentProject.id
      "%s:%s:%s> ".format (
        currProject, currBranch, Plugin.pluginVersion
      )
    }
  }
}

object Ls {

  import _root_.ls.Plugin.LsKeys._

  lazy val settings = _root_.ls.Plugin.lsSettings ++ Seq(
    (description in lsync) := "Scalr plugin for Play Framework 2",
    licenses in lsync <<= licenses,
    (tags in lsync) := Seq("play", "scalr", "resizing"),
    (docsUrl in lsync) := Some(new URL("https://github.com/digiPlant/play-scalr/wiki"))
  )
}
