package io.sapl.demo.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class DomainActions {

    private final List<String> actionList;
    private final boolean unrestrictedAccess;

    public static final DomainActions FULL = new DomainActions(Arrays.asList("create", "build", "read", "get",
            "find", "update", "modify", "delete", "destroy", "doNothing"), true);
    public static final DomainActions CRUD = new DomainActions(Arrays.asList("create", "read", "update", "delete"),
            true);
    public static final DomainActions READ_ONLY = new DomainActions(Collections.singletonList("read"),
            false);
    public static final DomainActions NONE = new DomainActions(Collections.emptyList(), false);


    public List<String> generateActionsForResource(String resource) {
        String capitalizedResource = StringUtils.capitalize(resource);
        return getActionList().stream().map(action -> action + capitalizedResource).collect(Collectors.toList());
    }

}
