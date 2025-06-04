# intellij-plugin-sandbox

![Build](https://github.com/lucernae/intellij-plugin-sandbox/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Get familiar with the [template documentation][template].
- [ ] Adjust the [pluginGroup](./gradle.properties) and [pluginName](./gradle.properties), as well as the [id](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [ ] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the `MARKETPLACE_ID` in the above README badges. You can obtain it once the plugin is published to JetBrains Marketplace.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
This IntelliJ Platform Plugin provides integration between Gherkin feature files and Go tests. It adds a "Run" button next to each Scenario in a Gherkin file. When clicked, the plugin will run a Go test with a test case specifier derived from the Scenario title.

Key features:
- Parse Gherkin-style files and identify Scenarios
- Provide a Run button in the IDE for each Scenario
- Run Go tests with test case specifiers derived from Scenario titles
- Display test output in the Run window like standard Go test commands

This plugin is useful for BDD (Behavior-Driven Development) workflows where Gherkin is used to define test scenarios and Go is used to implement the tests.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Usage

### Creating Gherkin Feature Files

1. Create a new file with a `.feature` extension
2. Write your feature file using Gherkin syntax:

```gherkin
Feature: Sample feature for testing

  Scenario: Add two numbers
    Given I have entered 50 into the calculator
    And I have entered 70 into the calculator
    When I press add
    Then the result should be 120 on the screen

  Scenario: Subtract two numbers
    Given I have entered 70 into the calculator
    And I have entered 50 into the calculator
    When I press subtract
    Then the result should be 20 on the screen
```

### Creating Go Test Files

Create Go test files that correspond to your Gherkin scenarios. The test function names should be derived from the scenario titles:

```go
package calculator

import (
	"testing"
)

// TestAddTwoNumbers is a test function that corresponds to the "Add two numbers" scenario
func TestAddTwoNumbers(t *testing.T) {
	// Implement the steps from the scenario
	result := 50 + 70
	if result != 120 {
		t.Errorf("Expected 120, got %d", result)
	}
}

// TestSubtractTwoNumbers is a test function that corresponds to the "Subtract two numbers" scenario
func TestSubtractTwoNumbers(t *testing.T) {
	// Implement the steps from the scenario
	result := 70 - 50
	if result != 20 {
		t.Errorf("Expected 20, got %d", result)
	}
}
```

### Running Tests

1. Open a Gherkin feature file in the editor
2. You'll see a "Run" button next to each Scenario
3. Click the "Run" button to execute the corresponding Go test
4. The test output will be displayed in the Run window

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "intellij-plugin-sandbox"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/lucernae/intellij-plugin-sandbox/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
