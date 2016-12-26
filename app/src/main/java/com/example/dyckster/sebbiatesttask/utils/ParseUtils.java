package com.example.dyckster.sebbiatesttask.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {


    public static String trimString(String string) {
        return trimString(string, 50);
    }

    public static String trimString(String string, int length) {
        if (string.length() <= length) return string;
        int pos = string.lastIndexOf(" ", length - 3);
        if (pos < 0) return string.substring(0, length);
        return string.substring(0, pos) + "...";
    }

    // 1234567 -> 1 234 567
    public static String addSpaces(String str) {
        StringBuilder tmp1 = new StringBuilder(str.replaceAll("\\s", "")).reverse();
        String tmp2 = tmp1.toString().replaceAll(".{3}", "$0 ");
        StringBuilder tmp3 = new StringBuilder(tmp2).reverse();
        return tmp3.toString();
    }

    // 1 234 567 -> 1234567
    public static String removeSpaces(String str) {
        return str.replaceAll("\\s", "");
    }

    public static final int objToInt(Object x) {
        if (x == null)
            return 0;

        if (x.equals(JSONObject.NULL))
            return 0;

        if (x instanceof String) {
            String s = (String) x;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                try {
                    Pattern p = Pattern.compile("(\\d+)\\.(\\d+)");
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        return Integer.parseInt(m.group(1));
                    } else {
                        return 0;
                    }
                } catch (Exception ei) {
                    return 0;
                }
            }
        } else if (x instanceof Number) {
            Number n = (Number) x;
            return n.intValue();
        } else {
            throw new ClassCastException("Cannot parse int from " + x.getClass().toString());
        }
    }


    public static final boolean objToBoolean(Object x) {
        if (x == null)
            return false;

        if (x.equals(JSONObject.NULL))
            return false;

        if (x instanceof String) {
            String s = (String) x;
            if (s.equals("1"))
                return true;
            try {
                return Boolean.parseBoolean(s);
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (x instanceof Number) {
            Number n = (Number) x;
            return n.intValue() == 0 ? false : true;
        } else if (x instanceof Boolean) {
            Boolean b = (Boolean) x;
            return b.booleanValue();
        } else {
            throw new ClassCastException("Cannot parse boolean from " + x.getClass().toString());
        }
    }

    public static final long objToLong(Object x, long def) {
        if (x == null)
            return def;

        if (x.equals(JSONObject.NULL))
            return def;

        if (x instanceof String) {
            String s = (String) x;
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return def;
            }
        } else if (x instanceof Number) {
            Number n = (Number) x;
            return n.longValue();
        } else {
            throw new ClassCastException("Cannot parse long from " + x.getClass().toString());
        }
    }

    public static final long objToLong(Object x) {
        return objToLong(x, 0L);
    }

    public static final double objToDouble(Object x) {
        if (x == null)
            return 0.0;

        if (x.equals(JSONObject.NULL))
            return 0.0;

        if (x instanceof String) {
            String s = (String) x;
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else if (x instanceof Number) {
            Number n = (Number) x;
            return n.doubleValue();
        } else {
            throw new ClassCastException("Cannot parse double from " + x.getClass().toString());
        }
    }

    public static final String objToStr(Object x) {
        if (x == null)
            return null;

        if (x.equals(JSONObject.NULL))
            return null;

        if (x instanceof String) {
            if (((String) x).equalsIgnoreCase("null")) {
                return null;
            } else {
                return (String) x;
            }
        } else if (x instanceof Number) {
            Number n = (Number) x;
            return n.toString();
        } else {
            throw new ClassCastException("Cannot parse string from " + x.getClass().toString());
        }
    }

    public static final JSONArray objToJSONArray(Object x) throws JSONException {
        if (x == null)
            return null;

        if (x.equals(JSONObject.NULL))
            return null;

        if (x instanceof JSONArray) {
            return (JSONArray) x;
        } else if (x instanceof JSONObject) {
            JSONObject obj = (JSONObject) x;
            ArrayList<String> names = new ArrayList<String>();
            @SuppressWarnings("unchecked")
            Iterator<String> iter = obj.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                names.add(key);
            }
            Collections.sort(names);
            JSONArray jsonNames = new JSONArray();
            for (String name : names) {
                jsonNames.put(name);
            }
            return obj.toJSONArray(jsonNames);
        } else {
            throw new ClassCastException("Cannot parse json array from " + x.getClass().toString());
        }
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:ss");

    public static Date parseDate(String date) {
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            return new Date();
        }
    }
}
