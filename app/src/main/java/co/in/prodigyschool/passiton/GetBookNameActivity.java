package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookNameActivity extends AppCompatActivity {

    boolean isTextbook;
    String bookName;
    EditText bookNameField;
    TextView nameQuestion, namingInstructuions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_name);
        getSupportActionBar().setTitle("List a book");
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK",true);
        nameQuestion = (TextView)findViewById(R.id.titleQTV);
        namingInstructuions = (TextView) findViewById(R.id.namingInstructions);
        if(!isTextbook) {
            nameQuestion.setText(R.string.notes_name_question);
            namingInstructuions.setText(R.string.othermaterial_naming_instructions);
        }
        bookNameField = (EditText) findViewById(R.id.bookName);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab12);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookName = bookNameField.getText().toString();
                if (bookName.length() < 10) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please enter at least 10 characters", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
                else {
                    Intent getBookDescription = new Intent(GetBookNameActivity.this, GetBookDescriptionActivity.class);
                    getBookDescription.putExtra("IS_TEXTBOOK", isTextbook);
                    getBookDescription.putExtra("BOOK_NAME",bookName);
                    startActivity(getBookDescription);
                }
            }
        });

    }
}
