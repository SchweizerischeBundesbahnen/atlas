import org.apache.tools.ant.taskdefs.condition.Os

tasks.register<Exec>("execNpmCi", fun Exec.() {
  doFirst {
    println("[Angular] Run atlas npm ci")
  }
  inputs.files(project.files("package.json"))
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "ci")
  outputs.files(project.files("package-lock.json"))
})

tasks.register<Exec>("execNpmLint", fun Exec.() {
  doFirst {
    println("[Angular] Run atlas npm lint")
  }
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "run", "lint")
  mustRunAfter(tasks.getByName("execNpmCi"))
})

tasks.register<Exec>("execNpmBuild", fun Exec.() {
  doFirst {
    println("[Angular] Run atlas npm Build")
  }
  inputs.files("package.json")
  inputs.dir("src")
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "run", "build-prod")
  mustRunAfter(tasks.getByName("execNpmLint"))
  outputs.dir("dist")
})

tasks.register<Exec>("execNpmTest", fun Exec.() {
  doFirst {
    println("[Angular] Run atlas npm Test")
  }
  var execNpmByPlatform = "npm"
  if (Os.isFamily(Os.FAMILY_WINDOWS)) {
    execNpmByPlatform = "npm.cmd"
  }
  commandLine(execNpmByPlatform, "run", "test")
  mustRunAfter(tasks.getByName("execNpmBuild"))
})

gradle.projectsEvaluated {
  tasks.register("build", fun Task.() {
    dependsOn(tasks.getByName("execNpmBuild"))
      .dependsOn(tasks.getByName("execNpmLint"))
      .dependsOn(tasks.getByName("execNpmCi"))
      .dependsOn(tasks.getByName("execNpmTest"))
  })
}

tasks.register<Exec>("clean", fun Exec.() {
  doFirst {
    println("[Angular] clean build dir")
  }
  commandLine("rm", "-rf", "dist")
})
