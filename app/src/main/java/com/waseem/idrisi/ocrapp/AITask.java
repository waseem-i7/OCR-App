package com.waseem.idrisi.ocrapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AITask {


    //You can use https://regexr.com website to write your own regular expression.
    public static String fetchAdhar(String input_str) {
        return readRegex(input_str, "(?<!\\d)\\d{4}(?:\\s\\d{4}){2}(?!\\d)")
                .replaceAll("\n", "")
                .replaceAll(" ", "")
                .trim();
    }

    public static String fetchPin(String input_str) {
        return readRegex(input_str, "([1-9]{1}[0-9]{5}|[1-9]{1}[0-9]{3}\\\\s[0-9]{3})");
    }

    public static String fetchMob(String input_str) {
        String str = "";
        str = readRegex(input_str, "(?<!\\d)\\d{10,14}(?!\\d)");
        str += ", " + readRegex(input_str, "\\b\\d{10,14}\\b");

        return str;
    }

    public static String fetchPolicyNo(String input_str) {
        String str = "";
        String arr[] = input_str.trim().split(" ");
        for (int x = 0; x < arr.length; x++) {
            String tmp = arr[x].replaceAll("\\/", "#").trim();
            System.out.println("refcc--tgh-------" + tmp);
            if (tmp.contains("P#")) {
                System.out.println("refcc--tgh1-------" + tmp);
                tmp = tmp.replaceAll("#", "\\/").trim();
                str += "# " + tmp;

            }
        }

        return str;
    }

    public static String fetchEmail(String input_str) {
        return readRegex(input_str, "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
    }

    public static String fetchDate(String input_str) {
        String str = "";
        str = readRegex(input_str, "(\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}/\\d{1,2})");
        str += ", " + readRegex(input_str, "(\\d{1,2}-\\d{1,2}-\\d{4}|\\d{1,2}-\\d{1,2})");

        return str;
    }

    public static String fetchAddress(String input_str) {
        String address = "";
        System.out.println("results----------addr--1--------");
        try {
            int start = input_str.indexOf("Address:");
            if (input_str.contains("Address:")) {
                start = input_str.indexOf("Address:");
            } else if (input_str.contains("Address")) {
                start = input_str.indexOf("Address");
            }
            if (start < 0) {
                start = 0;
            }
            System.out.println("results----------addr--2--------" + start);
            String pincode = fetchPin(input_str);
            int end = 0;
            if (pincode.length() > 0) {
                end = (input_str.indexOf(pincode) + pincode.length()) - 1;
                int index = input_str.indexOf(pincode);
                int last_index = input_str.lastIndexOf(pincode);
                while ((end < start) && index < last_index) {
                    index = input_str.indexOf(pincode, index + pincode.length());
                    end = index + pincode.length();
                }
                if (end < start) {
                    end = last_index + pincode.length();
                }
                if (end >= input_str.length()) {
                    end = input_str.length() - 1;
                }
                if ((end + 1) < input_str.length()) {
                    end++;
                }
            }

            System.out.println("results----------addr--3--------" + start + "-------" + end);
            address = input_str.substring(start, end);
            address = address.replaceAll("T, T: FIT TŽT, fta T:", "").trim();
            address = address.replaceAll("T/T/9TE: H, THZ af", "").trim();
            address = address.replaceAll("House/Bldg./Apt.:", " ").trim();
            address = address.replaceAll("Street/Road/Lane:", " ").trim();
            address = address.replaceAll("Village/Town/City:", " ").trim();
            address = address.replaceAll("P.O.:", " ").trim();
            address = address.replaceAll("State:", " ").trim();
            address = address.replaceAll("PinCode:", " ").trim();
            address = address.replaceAll("  ", " ").trim();
            System.out.println("results----------addr--4----" + address);
        } catch (Exception e) {

        }
        return address;
    }

    private static String readRegex(String input_str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input_str);
        String s = "";

        if (m.find()) {
            s = m.group(0);
        }
        return s;
    }

    public static String fetchPolicyDt(String input_str) {
        String str = "";
        try {
            System.out.println("refcc----------------" + input_str);
            System.out.println("refcc---------email-------" + fetchEmail(input_str));
            System.out.println("refcc---------mob-------" + fetchMob(input_str));
            System.out.println("refcc---------date-------" + fetchDate(input_str));
            System.out.println("refcc---------policy-------" + fetchPolicyNo(input_str));
            System.out.println("refcc---------address-------" + fetchAddress(input_str));

            str += "Email: " + fetchEmail(input_str)
                    + "\n\nMob: " + fetchMob(input_str)
                    + "\n\nDate: " + fetchDate(input_str)
                    + "\n\nPolicy: " + fetchPolicyNo(input_str)
                    + "\n\nAddress: " + fetchAddress(input_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String fetchPolicyNo1(String input_str) {
        String[] keywords = {"Policy Number", "Policy ID", "Policy Code", "policy reference no", "Policy No", "Policy No."};

        String policyNumber = null;

        for (String keyword : keywords) {
            int keywordIndex = input_str.indexOf(keyword);
            int startIndex = keywordIndex + keyword.length();
            while (Character.isWhitespace(input_str.charAt(startIndex))) {
                startIndex++;
            }
            int endIndex =  findLastWordIndex(input_str,startIndex);
            if (keywordIndex != -1 ) {
                // Assuming policy number follows the keyword immediately
                policyNumber = input_str.substring(startIndex,endIndex).trim();
                break;
            }
        }

        if (policyNumber != null) {
            // Apply additional validation or processing if needed
            // Display or use the policy number within your application
            return policyNumber;
        } else {
            // Policy number not found or extraction failed
            // Handle error or provide appropriate feedback to the user
        }
        return "";
    }

    public static String fetchInsurerName(String input_str) {
        String[] keywords = {"icic", "ICIC", "iffco-tokio",
                "IFFCO-TOKIO", "IFFCO TOKIO", "HDFC ERGO",
                "HDFC","Bajaj Allianz","Bajaj","bajaj"};

        String insurerName = null;

        for (String keyword : keywords) {
            int keywordStartIndex = input_str.indexOf(keyword);
            int keywordEndIndex = keywordStartIndex + keyword.length();

            if (keywordStartIndex != -1 ) {
                // Assuming policy number follows the keyword immediately
                insurerName = input_str.substring(keywordStartIndex,keywordEndIndex).trim();
            }
        }

        if (insurerName != null) {
            // Apply additional validation or processing if needed
            // Display or use the policy number within your application
            return insurerName;
        } else {
            // Policy number not found or extraction failed
            // Handle error or provide appropriate feedback to the user
        }
        return "";
    }



    private static int findLastWordIndex(String input, int firstIndex) {
        int endIndex = firstIndex;
        int length = input.length();

        // Skip non-word characters from the known first index
        while (endIndex < length && !Character.isWhitespace(input.charAt(endIndex))) {
            endIndex++;
        }

        // Find the last index of the word
        while (endIndex < length && Character.isWhitespace(input.charAt(endIndex))) {
            endIndex++;
        }

        return endIndex - 1;
    }

}
