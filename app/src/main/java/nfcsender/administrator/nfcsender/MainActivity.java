package nfcsender.administrator.nfcsender;

import android.annotation.TargetApi;
import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.Locale;


public class MainActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback{

    NfcAdapter mNfcAdapter = null; //nfcAdapter
    TextView mTextView; // mTextView
    // commit test
    // commit test
    // commit test
    // commit test
    // commit test
    // commit test
    // commit test
    // commit test2
    // commit test2
    // commit test2
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.textMessage);
        // nfc 어댑터 구함
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //  NFC 어댑터가 null이면(CLASS NOT FOUND) 통신불가
        if(mNfcAdapter != null){
            // NFC 통신가능할 경우. 상태창에 띄움.
            mTextView.setText("Tap to another NFC device. And touch Screen");
            Log.e("NfcAdapter is NULL : ","Tap to another NFC device. And touch Screen");
        } else {
            mTextView.setText("This phone is not NFC enable");
            Log.e("NfcAdapter NOT NULL : ", "This phone is not NFC enable");
        }

        // NDEF 메세지 생성하고 전송을 위한 콜백 함수 설정
        mNfcAdapter.setNdefPushMessageCallback(this,this);
        // NDEF 메시지 전송 완료 이벤트 콜백 함수 설정
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }

    // NDEF 메세지 생성
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.i("createNdefMessage : ", "called");
        // 여러 NDEF 레코드를 모아서 하나의 NDEF 메세지 생성
        NdefMessage message = new NdefMessage( new NdefRecord[]{
//                createTextRecord("Text sample record-1", Locale.ENGLISH),
//                createTextRecord("한국어 sample record-2", Locale.KOREAN),
//                createUriRecord("www.google.com"),
                createMimeRecord(),
                createPictureRecord()
        });
        return message;
    }

    private NdefRecord createPictureRecord() {
        Log.i("PictureRecord","CALLED");

        String msg = "" + "\n";

        byte[] picBytes = msg.getBytes();

        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA,"text/vcard".getBytes(),new byte[0], picBytes);
    }

    // MIME 형식 레코드 생성
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public NdefRecord createMimeRecord(){
        Log.i("createMimeRecord", "CALLED");
//        byte[] uriField = text.getBytes(Charset.forName("US-ASCII"));
//        byte[] payload = new byte[uriField.length+1];
//        payload[0] = 0x00; // ID코드 사용 안함.
//        System.arraycopy(uriField,0,payload,1,uriField.length);
        String msg = "BEGIN:VCARD" + "\n" // \n : CRLF(Carrage Return Line Feed)
                + "VERSION:2.1" + "\n"
                + "N:정재훈" + "\n" // N : 주소록 상세보기 이름
                + "FN:정재훈" + "\n" // FM : 주소록 노출 이름
                + "ORG:Budda Camp;기술팀;부장" + "\n" // ORG : 회사명;부서
                + "ADR;WORK:부산 해운대구 센텀그린타워 2104호" + "\n"
                + "TEL;CELL;PREF:010-1234-5678" + "\n" // CELL : 휴대폰 번호, PREF : 대표번호.
                + "TEL;WORK:051-547-7975" + "\n" // WORK : 직장 전화번호
                + "TEL;WORK;FAX:051-555-5556" + "\n" // WORK;FAX : 직장 팩스
                + "EMAIL;PREF;INTERNET:gForrest@example.com" + "\n"
                + "URL:www.website.com" + "\n" // URL : 웹사이트, 블로그 통합.
                + "URL:blog.blogtest.com" + "\n"
                + "END:VCARD" + "\n";
        byte[] textBytes = msg.getBytes(Charset.forName("UTF-8"));
        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(),new byte[0], textBytes);
    }

    // 텍스트 형식의 레코드 생성
    public NdefRecord createTextRecord(String text, Locale locale){
        Log.i("createTextRecord : ","CALLED");
        // 텍스트 데이터를 인코딩. byte 배열로 변환
        byte[] data = byteEncoding(text,locale);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,new byte[0], data);
    }

    // 텍스트 데이터를 인코딩해서 byte 배열로 변환
    public byte[] byteEncoding(String text, Locale locale){
        Log.i("byteEncoding : ","CALLED");
        // 언어 지정 코드 생성.
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        // 인코딩 형식
        Charset utfEncoding = Charset.forName("UTF-8");
        // 텍스트를 byte 배열로 변환
        byte[] textBytes = text.getBytes(utfEncoding);

        // 전송할 버퍼 생성
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) langBytes.length;
        // 버퍼에 언어 코드 저장
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //버퍼에 데이터 저장
        System.arraycopy(textBytes, 0, data, 1+langBytes.length, textBytes.length);
        return data;
    }

    // URI 형식의 레코드 생성
    public NdefRecord createUriRecord(String url) {
        Log.i("createUriRecord: ","CALLED");
        // URL 경로를 byte 배열로 변환 (..하면서 US-ASCII 형식으로 지정. )
        byte[] uriField = url.getBytes(Charset.forName("US-ASCII"));
        // URL 경로를 의미하는 WKT (Well-Known type) TNF : 1을 부여
        byte[] payload = new byte[uriField.length+1];
        // payload 부분의 ID값. 아래 코드 이후로 실제 데이터가 진행됨.
                /* payload 부분의 ID값. 아래 코드 이후로 실제 데이터가 진행됨.
        *  payload의 ID값을 따로 정해둔 이유는 http://www.naver.com을 보낸다고 할때,
        *  http://www. 부분이나
        *  https://www. 부분같이 중복되거나 자주쓰이는 부분을 간단히 하고 페이로드의 길이를 줄이면서 전송 데이터의 크기를 줄일 목적으로 만듬.
        */

        payload[0] = 0x01; // http://
//        payload[0] = 0x02; // https://
        System.arraycopy(uriField,0,payload,1,uriField.length);

        //NDEF 레코드 생성
        /*
        * CONSTRUCTOR NdefRecord(short tnf, byte[] type, byte[] id, byte[] payload)
        * short tnf --
        * 3비트의 TNF(Type Name Format)를 정의해놨음 TNF_WELL_KNOWN(0x01) 은  nfc forum에서 정의한 타입형식(WKT, NFC Forum-well-known type)
        *                                            TNF_MIME_MEDIA :         MIME 타입형식 (MIME media, text/vcard 등을 정의해놓은 tnf
        * byte[] type : 네번째 파라미터인 byte[] payload의 데이터 타입을 정의. URI, TEXT 등을 정의할 수 있음.
        *
        * byte[] id : 레코드가 여러개로 이루어져 있을 경우, 레코드 끼리의 참조를 위해 쓰이는 값. 참조 할일 없으니 0
        *
        * byte[] payload : 바이트 배열로 이루어져 NDEF 포맷으로 사용자가 전송하려고 하는 데이터
        * */
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_URI,new byte[0], payload);
    }

    // NDEF 메세지 전송 완료 이벤트 함수
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        Log.i("onNdefPushComplete : ","CALLED");
        // 핸들러에 메세지 전달
        mHandler.obtainMessage(1).sendToTarget();
    }

    // NDEF 메세지 전송이 완료되면 TextView에 결과를 표시함
    private Handler mHandler = new Handler() {
      @Override
      public void handleMessage(Message msg){
          switch(msg.what){
              case 1 : mTextView.setText("NDEF Message sending completed");
                  break;
          }
      }
    };

} // end