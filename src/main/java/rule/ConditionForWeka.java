package rule;

import java.util.HashMap;
import java.util.Map;

public class ConditionForWeka {


    private String field;
    private String value;
    private ConditionForWeka.Operator operator;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ConditionForWeka.Operator getOperator() {
        return operator;
    }

    public void setOperator(ConditionForWeka.Operator operator) {
        this.operator = operator;
    }

    /*
               switch (condition.getOperator()) {
                case EQUAL_TO:
                    operator = "==";
                    break;
                case NOT_EQUAL_TO:
                    operator = "!=";
                    break;
                case GREATER_THAN:
                    operator = ">";
                    break;
                case LESS_THAN:
                    operator = "<";
                    break;
                case GREATER_THAN_OR_EQUAL_TO:
                    operator = ">=";
                    break;
                case LESS_THAN_OR_EQUAL_TO:
                    operator = "<=";
                    break;
            }
     */

    public enum Operator {
        NOT_EQUAL_TO("NOT_EQUAL_TO", "!="),
        EQUAL_TO("EQUAL_TO", "=="),
        EQUAL("EQUAL", "="),
        GREATER_THAN("GREATER_THAN", ">"),
        LESS_THAN("LESS_THAN", "<"),
        GREATER_THAN_OR_EQUAL_TO("GREATER_THAN_OR_EQUAL_TO", ">="),
        LESS_THAN_OR_EQUAL_TO("LESS_THAN_OR_EQUAL_TO", "<=");
        private final String name;
        private final String value;

        public String getValue() {
            return value;
        }

        private static final Map<String, Operator> constants = new HashMap<String, Operator>();

        static {
            for (Operator c : values()) {

                constants.put(c.value, c);

            }
        }

        Operator(String name, String value) {
            this.value = value;
            this.name = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static Operator fromValue(String value) {
            Operator constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

}
