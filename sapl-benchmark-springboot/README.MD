# SAPl BENCHMARK

## Structured Random Benchmark

### Command Line Arguments

### Benchmark Configurations
Several predefined benchmark configurations are available as property files:
- application-15k.properties (will generate approx. 15.000 policies)
- application-30k.properties (will generate approx. 30.000 policies)
- application-40k.properties (will generate approx. 40.000 policies)

They can be activated by setting the respective profile using the parameter `-Dspring.profiles.active=profile`.


#### Provide external configuration
It is possible to provide a custom property file using the parameter `--spring.config.location=/whereever/appplication.properties`.

## Fully Random Benchmark