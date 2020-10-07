package tsukunesan.todo2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ボタン定義
        final Button createButton = findViewById(R.id.createButton);
        final Button deleteButton = findViewById(R.id.deleteButton);
        final EditText todoEdit = findViewById(R.id.todoEdit);
        //チェックリスト定義
        final ArrayList<Integer> chkIndex = new ArrayList<>();
        // todoリスト項目
        final ArrayList<String> data = new ArrayList<>();

        // ヘルパー準備
        final todoDatabaseHelper helper = new todoDatabaseHelper(this);
        // データベースからデータ取得
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            Cursor cs = db.query("todos", null, null, null, null, null, null);
            if (cs.moveToFirst()) {
                data.add(cs.getString(0));
                while (cs.moveToNext()) {
                    data.add(cs.getString(0));
                }
            }
        }

        // アダプター作成
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, data);
        // ListViewにデータを登録
        final ListView todoList = findViewById(R.id.todoList);
        todoList.setAdapter(adapter);

        // リストがクリックされたときのイベントリスナー
        todoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int count = 0;
                // List要素数だけループ
                for (Integer i = 0; i < adapter.getCount(); i++) {
                    // チェック項目確認
                    if (todoList.isItemChecked(i)) {
                        // deleteButtonを表示
                        deleteButton.setVisibility(View.VISIBLE);
                        count++;
                        if (!chkIndex.contains(i)) {
                            chkIndex.add(i);
                            Log.d("chkIndex", String.valueOf(chkIndex));
                        }
                    } else if (chkIndex.contains(i)) {
                        chkIndex.remove(i);
                        Log.d("chkIndex", String.valueOf(chkIndex));
                    }
                    // deleteButton非表示
                    if (count == 0) {
                        deleteButton.setVisibility(View.INVISIBLE);
                        count = 0;
                    }
                }
            }
        });

        // createButtonのクリックイベントリスナー
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // データベースにデータ挿入
                // EditTextに入力がないときはToast表示
                if (todoEdit.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "ToDoが入力されていません", Toast.LENGTH_SHORT).show();
                } else {
                    try (SQLiteDatabase db = helper.getWritableDatabase()) {
                        ContentValues cv = new ContentValues();
                        cv.put("todo", todoEdit.getText().toString());
                        db.insert("todos", null, cv);
                        Cursor cs = db.query("todos", null, null, null, null, null, null);
                        if (cs.moveToLast()) {
                            data.add(cs.getString(0));
                        }
                    }
                    // リスト再描画
                    todoEdit.setText("", TextView.BufferType.NORMAL);
                    todoList.setAdapter(adapter);
                }

            }
        });

        // deleteButtonのクリックイベントリスナー
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chkIndexをソート（IndexOutOfBoundsExceptionエラーを避けるため）
                Collections.sort(chkIndex, Collections.<Integer>reverseOrder());
                for (int chkData : chkIndex) {
                    Log.d("chkData", String.valueOf(chkData));
                    // チェックマークを削除
                    todoList.setItemChecked(chkData, false);
                    // 削除するdataを取得＆削除
                    String deleteData = data.get(chkData);
                    String[] deletelist = new String[1];
                    deletelist[0] = deleteData;
                    try (SQLiteDatabase db = helper.getWritableDatabase()) {
                        db.delete("todos", "todo=?", deletelist);
                    }
                    adapter.remove(deleteData);
                }
                // チェック項目リセット&deleteButton非表示
                chkIndex.clear();
                deleteButton.setVisibility(View.INVISIBLE);
            }
        });
    }
}
