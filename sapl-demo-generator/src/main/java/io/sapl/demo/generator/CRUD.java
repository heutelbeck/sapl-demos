package io.sapl.demo.generator;

import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Value
public class CRUD {

    public static final CRUD ALL = new CRUD(true, true, true, true);

    public static final CRUD NONE = new CRUD(false, false, false, false);

    public static final CRUD READ_ONLY = new CRUD(false, true, false, false);

    boolean create;

    boolean read;

    boolean update;

    boolean delete;

    public List<String> generateActionsWithName(String name) {
        List<String> actions = new ArrayList<>();
        if (create) actions.add("create" + StringUtils.capitalize(name));
        if (read) actions.add("get" + StringUtils.capitalize(name));
        if (update) actions.add("update" + StringUtils.capitalize(name));
        if (delete) actions.add("delete" + StringUtils.capitalize(name));

        return actions;
    }
}
