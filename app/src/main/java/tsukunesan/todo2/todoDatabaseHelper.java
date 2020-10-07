package tsukunesan.todo2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class todoDatabaseHelper extends SQLiteOpenHelper {
    static final private String DBNAME = "todolist.sqlite";
    static final private int VERSION = 1;

    // コンストラクター
    todoDatabaseHelper(Context context){
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE todos (" + "todo TEXT PRIMARY KEY)");
        db.execSQL("INSERT INTO todos(todo, tabNo)" + " VALUES('todo1')");
        db.execSQL("INSERT INTO todos(todo)" + " VALUES('todo2')");
        db.execSQL("INSERT INTO todos(todo)" + " VALUES('todo3')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS todos");
        onCreate(db);
    }
}
