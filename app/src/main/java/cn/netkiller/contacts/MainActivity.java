package cn.netkiller.contacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText phone;
    private Button contact;
    private Button call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        contact = (Button) findViewById(R.id.contact);
        contact.setOnClickListener(this);

        call = (Button) findViewById(R.id.call);
        call.setOnClickListener(this);

        phone = (EditText) findViewById(R.id.phone);
        phone.setText("", TextView.BufferType.EDITABLE);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact:
                Toast.makeText(MainActivity.this, "获取联系人手机号码", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
                break;
            case R.id.call:
                Toast.makeText(MainActivity.this, "打电话", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);//ACTION_DIAL
                intent.setData(Uri.parse("tel:" + phone.getText()));
                startActivity(intent);

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
            cursor.moveToFirst();

            //条件为联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
            Cursor contact = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (contact.moveToNext()) {
                String contactNumber = contact.getString(contact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                phone.setText(contactNumber);
            }
            cursor.close();

        }
    }

}
