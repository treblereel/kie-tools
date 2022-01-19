import { workflowBuilder, injectstateBuilder, Specification } from "@severlessworkflow/sdk-typescript";

const workflow: Specification.Workflow = workflowBuilder()
  .id("helloworld")
  .specVersion("0.8")
  .version("1.0")
  .name("Hello World Workflow")
  .description("Inject Hello World")
  .start("Hello State")
  .states([
    injectstateBuilder()
      .name("Hello State")
      .data({
        result: "Hello World!",
      })
      .build(),
  ])
  .build();

console.log(workflow.id);
console.log(workflow.name);
console.log(workflow.description);
console.log(workflow.start);
