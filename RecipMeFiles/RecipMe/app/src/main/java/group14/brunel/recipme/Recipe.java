package group14.brunel.recipme;

/**
 * Created by jayalam on 08/02/2017.
 */

public class Recipe {

    private String title;
    private String desc;
    private String image;
    private String prep_time;
    private String cook_time;

    public Recipe(){

    }

    public Recipe(String title, String desc, String image, String prep_time, String cook_time){
        this.title = title;
        this.desc = desc;
        this.prep_time = prep_time;
        this.cook_time = cook_time;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCook_time() {
        return cook_time;
    }

    public void setCook_time(String cook_time) {
        this.cook_time = cook_time;
    }

    public String getPrep_time() {
        return prep_time;
    }

    public void setPrep_time(String prep_time) {
        this.prep_time = prep_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
