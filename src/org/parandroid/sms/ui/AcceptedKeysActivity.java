package org.parandroid.sms.ui;

import java.util.HashMap;

import org.parandroid.encryption.MessageEncryptionFactory;
import org.parandroid.sms.R;
import org.parandroid.sms.data.Contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AcceptedKeysActivity extends Activity {

	private static final String TAG = "PublicKeyManagerActivity";
    private static final int CONTEXT_MENU_DELETE = 0;

    private String[] descriptions;
    private Integer[] ids;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.accepted_keys_activity);
	}

    @Override
    public void onResume(){
    	super.onResume();
    	init();
    }
    
    private void init(){
        HashMap<Integer, String> items = MessageEncryptionFactory.getPublicKeyList(this);
	    ListView publicKeysList = (ListView) findViewById(R.id.public_keys);
	    
        if(items.size() == 0){
        	descriptions = new String[]{ getString(R.string.no_public_keys) };
        	unregisterForContextMenu(publicKeysList);
        }else{
        	registerForContextMenu(publicKeysList);
            Integer[] intArray = {};
            String[] stringArray = {};
            ids = items.keySet().toArray(intArray);
            descriptions = items.values().toArray(stringArray);
            
            for(int i = 0; i < descriptions.length; i++){
            	Contact c = Contact.get(descriptions[i], true);
            	if(c != null){
            		String name = c.getName();
            		if(name != null && !name.equals(descriptions[i])){
            			descriptions[i] = name + " <" + descriptions[i] + ">";
            		}
            	}
            }
        }

        final String[] descriptionList = this.descriptions;
	    final ArrayAdapter<String> publicKeys = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, descriptionList);
	    publicKeysList.setAdapter(publicKeys);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CONTEXT_MENU_DELETE, 0, getString(R.string.delete_public_key));
    }

    public boolean onContextItemSelected(MenuItem item){
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case CONTEXT_MENU_DELETE:
                AlertDialog.Builder generateKeypairDialogBuilder = new AlertDialog.Builder(this);
        	    generateKeypairDialogBuilder.setMessage(getText(R.string.delete_public_key_dialog))
        		   .setTitle(descriptions[info.position])
        		   .setCancelable(false)
        	       .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
                            MessageEncryptionFactory.deletePublicKey(AcceptedKeysActivity.this, ids[info.position]);
                            init();
        	           }
        	       })
        	       .setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	
        	AlertDialog alert = generateKeypairDialogBuilder.create();
        	alert.show();

            return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
