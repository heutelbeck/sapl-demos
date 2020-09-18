package io.sapl.demo.generator;

import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Value
public class DomainActions {

    public static final DomainActions ALL = new DomainActions(true, true, true, true);
    public static final DomainActions NONE = new DomainActions(false, false, false, false);
    public static final DomainActions READ_ONLY = new DomainActions(false, true, false, false);

    boolean create;

    boolean read;

    boolean update;

    boolean delete;

    public List<String> generateActionsForResource(String name) {
        List<String> actions = new ArrayList<>();
        if (create) actions.add("create" + StringUtils.capitalize(name));
        if (read) actions.add("get" + StringUtils.capitalize(name));
        if (update) actions.add("update" + StringUtils.capitalize(name));
        if (delete) actions.add("delete" + StringUtils.capitalize(name));

        return actions;
    }
}
