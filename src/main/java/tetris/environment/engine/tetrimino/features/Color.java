package tetris.environment.engine.tetrimino.features;


public enum Color {
    BLACK("#000000"),
    INDIANRED("#CD5C5C"),
    CORNFLOWERBLUE("#6495ED"),
    SLATEGRAY("#708090"),
    MEDIUMTURQUOISE("#48D1CC"),
    DARKGOLDENROD("#B8860B"),
    MEDIUMPURPLE("#9370DB"),
    FORESTGREEN("#228B22");

    private final String colorHexString;

    Color(final String colorHexString){
        this.colorHexString = colorHexString;
    }

    public String getHexString(){
        return colorHexString;
    }
}
