package io.doodler.jdbc.impexp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: SqlTextUtils
 * @Author: Fred Feng
 * @Date: 01/04/2023
 * @Version 1.0.0
 */
@UtilityClass
public class SqlTextUtils {

    public String addEscapeChar(String str, char escapeChar) {
        StringBuilder content = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == '\'') {
                content.append(escapeChar);
            }
            content.append(c);
        }
        return content.toString();
    }

    public List<String> addEndMarks(Collection<String> list) {
        List<String> copy = new ArrayList<>(list);
        ListIterator<String> iter = copy.listIterator();
        String sql;
        while (iter.hasNext()) {
            sql = iter.next();
            if (StringUtils.isBlank(sql)) {
                iter.remove();
            } else {
                iter.set(sql.trim() + ";");
            }
        }
        return copy;
    }
}