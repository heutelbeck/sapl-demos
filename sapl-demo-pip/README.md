# Tutorial  sapl-demo-pip

This demo demonstrates the usage of a Policy Information Point (PIP).  When evaluating a Sapl Policy a PIP may be used to provide  Information that is not included in the Request.

## Use Case
In these example use case, we will implement the rule: user may only see the patients room number, if they are one of its related persons.

These rule is expressed through these policy (see pipPolicy.sapl):

```java
policy "permit_relative_see_room_number"
permit
   action == "viewRoomNumber"
where
  subject.name in resource.id.<patient.related>;
  
```

We provide two Patients, Lenny and Karl. Lenny has one relative registered in our system, it is Dominik. Karl has three, it´s Julia, Alina and Janosch. Only these 
accounts are able to see the room number of their respective relative.

## Try it out
Login with one of the users: Dominik, Julia, Peter, Alina, Thomas, Brigitte, Janosch, Janina or Horst. 
The Password is allways "password". You can change this by providing another password in the application.properties. Not the plain value is needed but the bcrypted one (for example see https://www.dailycred.com/article/bcrypt-calculator).

## Policy Information Point

To implement a PIP, the class has to be annotated with `@PolicyInformationPoint`.
Here you can see the [PatientPIP](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-shared/src/main/java/io/sapl/demo/shared/pip/PatientPIP.java) from submodule <https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-shared>.


```java
@PolicyInformationPoint(name="patient", description="retrieves information about patients")
public class PatientPIP {

	private Optional<RelationRepo> relationRepo = Optional.empty();

	private final ObjectMapper om = new ObjectMapper();

	private RelationRepo getRelationRepo(){
		LOGGER.debug("GetRelationRepo...");
		if(!relationRepo.isPresent()){
			LOGGER.debug("RelRepo not present...");
			ApplicationContext context =
			      ApplicationContextProvider.getApplicationContext();
			LOGGER.debug("Context found: {}", context);
			relationRepo = 
Optional.of(ApplicationContextProvider.getApplicationContext().getBean(RelationRepo.class)); (1.)
		}
		LOGGER.debug("Found required instance of RelationRepo: {}",
		              relationRepo.isPresent());
		return relationRepo.get();
	}

	@Attribute(name="related")  (2.)
	public JsonNode getRelations (JsonNode value, Map<String, JsonNode> variables) {
		List<String> returnList = new ArrayList<>();
		try{
			int id = Integer.parseInt(value.asText());
			LOGGER.debug("Entering getRelations. ID: {}", id);

			returnList.addAll(getRelationRepo().findByPatientid(id).stream()
                      .map(Relation::getUsername)
                      .collect(Collectors.toList()));

		}catch(NumberFormatException e){
			LOGGER.debug("getRelations couldn't parse the value to Int", e);
		}
		JsonNode result = om.convertValue(returnList, JsonNode.class);
		LOGGER.debug("Result: {}", result);
		return result;
	}
}

```

1. The CrudRepository `RelationRepo` has not  been provided as bean at the time if  we want to access it via the PIP.
    Therefore we use _lazy initialization_ to load it.
    On the other  hand the [ApplicationContextProvider](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-shared/src/main/java/io/sapl/demo/shared/pip/ApplicationContextProvider.java)
    has to be loaded as  bean in submodule `sapl-demo-permeval`
    as you can see in [MvcConfig](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-pip/src/main/java/io/sapl/demo/pip/config/MvcConfig.java) :

        @Bean
        public ApplicationContextProvider applicationContextProvider(){
            return new ApplicationContextProvider();
        }
 
     Tip: Try to disable  `spring-boot-devtools` if `ApplicationContext` doesn't support `getBean(RelationRepo.class)`.

2. The method annotated with `@Attribute` gives back a list of users who are related to a patient. The corresponding policy looks like this:

        policy "permit_relative_see_room_number"
        permit
           action == "viewRoomNumber"
        where
          subject.name in resource.id.<patient.related>;





The PIP also has to be imported into the policy (the .sapl-Files) set with:

```
    import io.sapl.demo.shared.pip.PatientPIP as patient

```



Furthermore, the name of the PIP has to be notated in the _attributeFinders_ entry of the `pdp.json` as follows:

```json
{
    "algorithm": "DENY_UNLESS_PERMIT",
    "variables": {},
    "attributeFinders": ["patient"],
    "libraries": []
}
```

## Howto wire a PolicyInformationPoint?
The PIP (PolicyInformationPoint) in this example is automatically wired to the embedded PDP (PolicyDecisionPoint), as it lies in the default scan package `io.sapl`.
If you want to use your own PDP, you have to provide a Bean of interface type `io.sapl.spring.PIPProvider`. 
As this is a functional interface, a bean declaration could simply look like

```
@Bean
	PIPProvider myCustomPIPProvider(){
		return () -> Arrays.asList(MyPIP1.class, MyPIP2.class);
	}
```