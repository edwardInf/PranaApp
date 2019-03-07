package pranaproject.pranaapp.models;

public class Option {

    int mIconResource;
    String option;


    public Option(int iconResource, String opt){
        mIconResource = iconResource;
        option = opt;
    }

    public int getmIconResource() {
        return mIconResource;
    }

    public void setmIconResource(int mIconResource) {
        this.mIconResource = mIconResource;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

}
