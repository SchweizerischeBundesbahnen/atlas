import org.apache.tools.ant.taskdefs.condition.Os

task<Exec>("execNpmCi") {
  onlyIf("Exec npm ci only on Tekton"){
    System.getenv("UE_EXECUTION_SYSTEM") == "tekton"
  }
  doFirst {
    println("[Angular] Run atlas npm ci")
  }
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "ci")
}

task<Exec>("execNpmLint") {
  doFirst {
    println("[Angular] Run atlas npm lint")
  }
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "run", "lint")
  mustRunAfter(tasks.getByName("execNpmCi"))
}

task<Exec>("execNpmBuild") {
  doFirst {
    println("[Angular] Run atlas npm Build")
  }
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "run", "build-prod")
  mustRunAfter(tasks.getByName("execNpmLint"))
}

task<Exec>("execNpmTest") {
  doFirst {
    println("[Angular] Run atlas npm Test")
  }
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "run", "test")
  mustRunAfter(tasks.getByName("execNpmBuild"))
}

gradle.projectsEvaluated {
  task("build") {
    dependsOn(tasks.getByName("execNpmBuild")).
    dependsOn(tasks.getByName("execNpmLint")).
    dependsOn(tasks.getByName("execNpmCi")).
    dependsOn(tasks.getByName("execNpmTest"))
  }
}

task<Exec>("clean") {
  doFirst {
    println("[Angular] clean build dir")
  }
  commandLine("rm", "-rf", "dist")
}

task<Exec>("asd") {

  doFirst {
    println("[Angular] clean build dir")
  }
  commandLine("rm", "-rf", "dist")
}
