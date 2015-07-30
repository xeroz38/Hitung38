package com.zhack.poskasir;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.util.ItemProvider;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class MasterItemDetailActivity extends Activity implements View.OnClickListener {

    private static final int SELECT_PICTURE = 100;

    private String mImageName;
    private ImageView mItemImg;
    private EditText mNameEdit, mCategoryEdit, mPriceEdit;
    private Button mSaveBtn;
    private Bitmap mPhotoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_item_detail);

        mItemImg = (ImageView) findViewById(R.id.item_image);
        mNameEdit = (EditText) findViewById(R.id.name_edit);
        mCategoryEdit = (EditText) findViewById(R.id.category_edit);
        mPriceEdit = (EditText) findViewById(R.id.price_edit);
        mSaveBtn = (Button) findViewById(R.id.save_btn);
        mItemImg.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                mPhotoBitmap = (Bitmap) data.getExtras().get("data");
                mItemImg.setImageBitmap(Bitmap.createScaledBitmap(mPhotoBitmap, 300, 300, true));
            }
        }
    }

    private void insertItemListData(String title, String image, String category, String price) {
        ContentValues values = new ContentValues();
        values.put(Item.ITEM_TITLE, title);
        values.put(Item.ITEM_IMAGE, image);
        values.put(Item.ITEM_CATEGORY, category);
        values.put(Item.ITEM_PRICE, price);

        getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, values);
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/poskasir/img");
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/poskasir/img/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File("/sdcard/poskasir/img/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_image: {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String pickTitle = "Select or take a new Picture"; // Or get from strings.xml

                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
                startActivityForResult(chooserIntent, SELECT_PICTURE);

                break;
            }
            case R.id.save_btn: {
                mImageName = "IMG_" + System.currentTimeMillis();
                createDirectoryAndSaveFile(mPhotoBitmap, mImageName);
                insertItemListData(mNameEdit.getText().toString(),
                        mImageName,
                        mCategoryEdit.getText().toString(),
                        mPriceEdit.getText().toString());

                break;
            }
            default: break;
        }
    }
}
