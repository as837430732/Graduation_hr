package zxing.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.graduation_hr.R;

import zxing.decode.DecodeThread;


public class ResultActivity extends Activity {


	private TextView mResultText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Bundle extras = getIntent().getExtras();


		mResultText = (TextView) findViewById(R.id.result_text);

		if (null != extras) {

			String result = extras.getString("result");
			mResultText.setText(result);

		}
	}
}
