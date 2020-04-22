package group18.dashboard.util;

import group18.dashboard.database.enums.*;

public class Parsing {

    public static Enum<? extends Enum<?>> toEnum(String s) {
        switch (s) {
            case "<25":
                return ImpressionAge._3c25;
            case "25-34":
                return ImpressionAge._25_34;
            case "35-44":
                return ImpressionAge._35_44;
            case "45-54":
                return ImpressionAge._45_54;
            case ">54":
                return ImpressionAge._3e54;
            case "Social Media":
                return ImpressionContext.Social_Media;
            case "News":
                return ImpressionContext.News;
            case "Shopping":
                return ImpressionContext.Shopping;
            case "Blog":
                return ImpressionContext.Blog;
            case "Hobbies":
                return ImpressionContext.Hobbies;
            case "Travel":
                return ImpressionContext.Travel;
        }
        return null;
    }

}
