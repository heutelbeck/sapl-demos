package io.sapl.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class DomainActions {

    private final List<String> actionList;
    private final boolean unrestrictedAccess;

    public static final DomainActions CRUD = new DomainActions(Arrays.asList("create", "read", "update", "delete"),
            false);
    public static final DomainActions READ_ONLY = new DomainActions(Collections.singletonList("read"),
            false);
    public static final DomainActions NONE = new DomainActions(Collections.emptyList(), false);


    public List<String> generateActionsForResource(String resource) {
        return getActionList().stream().map(action -> action + resource).collect(Collectors.toList());
    }


    public static List<String> generateCustomActionList(int actionCount) {
        List<String> actionList = new ArrayList<>();

        for (int i = 0; i < new Random().nextInt(actionCount) + 1; i++) {
            actionList.add(String.format("action.%03d", new Random().nextInt(1000) + 1));
        }

        return new DomainActions(actionList, true).getActionList();
    }

    public static DomainActions generateActionListByCount(int actionCount) {
        List<String> actionList = new ArrayList<>();

        for (int i = 0; i < actionCount; i++) {
            actionList.add(String.format("action.%03d", i));
        }

        return new DomainActions(actionList, true);
    }
}
