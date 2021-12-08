package kr.co.oliveandwine.ssgether.Adapter.Item;

public class InfoItem {
    public InfoItem(int imageResId, String strCountry) {
        mImageResId = imageResId;
        mStrCountry = strCountry;
    }

    private int mImageResId;

    private String mStrCountry;

    public void setImageResId(int imageResId) {
        mImageResId = imageResId;
    }

    public int getImageResId() {
        return mImageResId;
    }

    public void setInfo(String strCountry) {
        mStrCountry = strCountry;
    }

    public String getInfo() {
        return mStrCountry;
    }
}
