import { Specification } from "@severlessworkflow/sdk-typescript";

const source = `id: helloworld
version: '1.0'
specVerion: '0.8'
name: Hello World Workflow
description: Another Inject Hello World
start: Hello State
states:
  - type: inject
    name: Hello State
    data:
      result: Hello World!
    end: true`;

const workflow: Specification.Workflow = Specification.Workflow.fromSource(source);

console.log(workflow.id);
console.log(workflow.name);
console.log(workflow.description);
console.log(workflow.start);
