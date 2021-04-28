package io.sapl.generator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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


    public static List<String> generateCustomActionList(DomainData domainData) {
        List<String> actionList = new ArrayList<>();

        for (int i = 0; i < domainData.getDice().nextInt(domainData.getNumberOfActions()) + 1; i++) {
            //            actionList.add(String.format("action.%03d", domainData.getDice().nextInt(1000) + 1));
            actionList.add(getRandomActionFromList(domainData));
        }

        return new DomainActions(actionList, true).getActionList();
    }

    private static String getRandomActionFromList(DomainData domainData) {
        return domainData.getDomainActions().get(domainData.getDice().nextInt(domainData.getDomainActions().size()));
    }

    public static List<String> generateActionListByCount(int actionCount) {
        List<String> actionList = new ArrayList<>();

        for (int i = 0; i < actionCount; i++) {
            actionList.add(String.format("action.%03d", i));
        }

        return actionList;
    }
}
