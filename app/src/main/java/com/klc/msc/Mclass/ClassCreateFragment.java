package com.klc.msc.Mclass;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.klc.msc.db.MStatus;
import com.klc.msc.R;
import com.klc.msc.db.contract.MSC_Contract;
import com.klc.msc.db.helper.MSC_Helper;
import com.klc.msc.utils.DateTimeUtils;
import com.klc.msc.utils.RegexUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static java.lang.String.format;

public class ClassCreateFragment extends Fragment implements View.OnClickListener
{
    private        MSC_Helper mscHelper;
    private static Calendar   calendar;
    private static int        year, month, dayOfMonth;
    private String title, startDate, endDate, startTime, endTime;

    private EditText etClassName, etClassPrice, etClassDescription, etClassLocation,
            etClassHour, etClassLessonNumber, etClassStudentNumber;

    private AlertDialog hourAlertDialog;

    private com.beardedhen.androidbootstrap.BootstrapButton
            btnClassStartDate, btnClassEndDate, btnClassStartTime, btnClassEndTime,
            btnCreateClass, btnClearForm;

    private static final int START_DATE = 0, START_TIME = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_class_create, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnClassStartDate:
                createDialog(START_DATE);
                break;
            case R.id.btnClassStartTime:
                createDialog(START_TIME);
                break;
            case R.id.btnCreateClass:
                // TODO: share to facebook
                try {
                    if (checkForm()) {
                        createClass();
                    }
                } catch (Exception e) {
                    AlertDialog.Builder error = new AlertDialog.Builder(getActivity());
                    error.setTitle("Error!");
                    error.setMessage(e.getMessage());
                    error.setCancelable(false);
                    error.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    error.show();
                }
                break;
            case R.id.btnClearForm:
                clearForm();
                break;
        }
    }

    private void initView(View view)
    {
        etClassName = (EditText) view.findViewById(R.id.etClassName);
        etClassPrice = (EditText) view.findViewById(R.id.etClassPrice);
        etClassDescription = (EditText) view.findViewById(R.id.etClassDescription);
        etClassLocation = (EditText) view.findViewById(R.id.etClassLocation);
        etClassHour = (EditText) view.findViewById(R.id.etClassHour);
        etClassLessonNumber = (EditText) view.findViewById(R.id.etClassLessonNumber);
        etClassStudentNumber = (EditText) view.findViewById(R.id.etClassStudentNumber);

        btnClassStartDate =
                (com.beardedhen.androidbootstrap.BootstrapButton) view.findViewById(R.id.btnClassStartDate);
        btnClassEndDate =
                (com.beardedhen.androidbootstrap.BootstrapButton) view.findViewById(R.id.btnClassEndDate);
        btnClassStartTime =
                (com.beardedhen.androidbootstrap.BootstrapButton) view.findViewById(R.id.btnClassStartTime);
        btnClassEndTime =
                (com.beardedhen.androidbootstrap.BootstrapButton) view.findViewById(R.id.btnClassEndTime);
        btnCreateClass =
                (com.beardedhen.androidbootstrap.BootstrapButton) view.findViewById(R.id.btnCreateClass);
        btnClearForm =
                (com.beardedhen.androidbootstrap.BootstrapButton) view.findViewById(R.id.btnClearForm);

        btnClassStartDate.setEnabled(false);
        btnClassStartDate.setOnClickListener(this);

        btnClassEndDate.setEnabled(false);

        btnClassStartTime.setEnabled(false);
        btnClassStartTime.setOnClickListener(this);

        btnClassEndTime.setEnabled(false);

        btnCreateClass.setOnClickListener(this);
        btnClearForm.setOnClickListener(this);

        etClassLessonNumber.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0) {
                    String tips1 = "Click to set date";
                    String tips2 = "Please choose MClass starting date";
                    btnClassStartDate.setText(tips1);
                    btnClassEndDate.setText(tips2);
                    btnClassStartDate.setEnabled(true);
                } else {
                    String tips = "Please choose a week";
                    btnClassStartDate.setText(tips);
                    btnClassEndDate.setText(tips);
                    btnClassStartDate.setEnabled(false);
                }
            }
        });

        etClassHour.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0) {
                    String tips1 = "Click to set time";
                    String tips2 = "Please choose MClass starting time";
                    btnClassStartTime.setText(tips1);
                    btnClassEndTime.setText(tips2);
                    btnClassStartTime.setEnabled(true);
                } else {
                    String tips = "Please enter how many hour(s) of lesson";
                    btnClassStartTime.setText(tips);
                    btnClassEndTime.setText(tips);
                    btnClassStartTime.setEnabled(false);
                }
            }
        });
    }

    private Boolean checkForm() throws Exception
    {
        if (RegexUtils.isEmpty(etClassName.getText().toString())) {
            throw new Exception("MClass name cannot be empty !");
        }
        if (RegexUtils.isEmpty(etClassPrice.getText().toString())) {
            throw new Exception("Price cannot be empty !");
        }
        if (RegexUtils.isEmpty(etClassDescription.getText().toString())) {
            throw new Exception("Description cannot be empty !");
        }
        if (RegexUtils.isEmpty(etClassLocation.getText().toString())) {
            throw new Exception("Location cannot be empty !");
        }
        if (RegexUtils.isEmpty(etClassLessonNumber.getText().toString())) {
            throw new Exception("Lesson number cannot be empty !");
        }
        if (RegexUtils.isEmpty(etClassHour.getText().toString())) {
            throw new Exception("Hour(s) cannot be empty !");
        }
        if (RegexUtils.isEmpty(etClassStudentNumber.getText().toString())) {
            throw new Exception("Maximum student number cannot be empty !");
        }
        if (!RegexUtils.isFormatedDate(btnClassStartDate.getText().toString())) {
            throw new Exception("MClass start date cannot be empty !");
        }
        if (!RegexUtils.isFormatedTime(btnClassStartTime.getText().toString())) {
            throw new Exception("MClass start time cannot be empty !");
        }

        return true;
    }

    private void createClass()
    {
        //classHelper = new ClassHelper(getActivity());
        //SQLiteDatabase db     = classHelper.getWritableDatabase();
        mscHelper = new MSC_Helper(getActivity());
        SQLiteDatabase db = mscHelper.getWritableDatabase();
        ContentValues  values = new ContentValues();

        values.put(MSC_Contract.MSCEntry.COL_NAME, etClassName.getText().toString());
        values.put(MSC_Contract.MSCEntry.COL_PRICE, Integer.parseInt(etClassPrice.getText().toString()));
        values.put(MSC_Contract.MSCEntry.COL_DESCRIPTION, etClassDescription.getText().toString());
        values.put(MSC_Contract.MSCEntry.COL_LOCATION, etClassLocation.getText().toString());
        values.put(MSC_Contract.MSCEntry.COL_LESSON_NO, Integer.parseInt(etClassLessonNumber.getText().toString()));
        values.put(MSC_Contract.MSCEntry.COL_HOURS, Integer.parseInt(etClassHour.getText().toString()));
        values.put(MSC_Contract.MSCEntry.COL_MAX_STUDENT_NO, Integer.parseInt(etClassStudentNumber.getText().toString()));
        values.put(MSC_Contract.MSCEntry.COL_START_DATE, btnClassStartDate.getText().toString());
        values.put(MSC_Contract.MSCEntry.COL_END_DATE, btnClassEndDate.getText().toString());
        values.put(MSC_Contract.MSCEntry.COL_START_TIME, btnClassStartTime.getText().toString());
        values.put(MSC_Contract.MSCEntry.COL_END_TIME, btnClassEndTime.getText().toString());
        values.put(MSC_Contract.MSCEntry.COL_STATUS_ID, MStatus.OPEN);
        values.put(MSC_Contract.MSCEntry.COL_CREATED_AT, DateTimeUtils.getDateTime());
        values.put(MSC_Contract.MSCEntry.COL_UPDATED_AT, DateTimeUtils.getDateTime());

        db.insert(MSC_Contract.MSCEntry.TABLE_CLASS, null, values);
        clearForm();
        finishInsert();
        db.close();

    }

    private void finishInsert()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Success !");
        dialog.setMessage("MClass has been created !");
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        dialog.show();
    }

    private void clearForm()
    {
        etClassName.setText("");
        etClassPrice.setText("");
        etClassDescription.setText("");
        etClassLocation.setText("");
        etClassHour.setText("");
        etClassLessonNumber.setText("");
        etClassStudentNumber.setText("");
        btnClassStartDate.setText("Please choose a week");
        btnClassEndDate.setText("Please choose a week");
        btnClassStartTime.setText("Please enter how many hour(s) of lesson");
        btnClassEndTime.setText("Please enter how many hour(s) of lesson");
    }

    private void createDialog(int id)
    {
        switch (id) {
            case START_DATE:
                calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("zh", "HK"));

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        startDate = simpleDateFormat.format(calendar.getTime());
                        btnClassStartDate.setText(startDate);

                        calendar.add(Calendar.DATE, 7 * (Integer.parseInt(etClassLessonNumber.getText().toString()) - 1));
                        endDate = simpleDateFormat.format(calendar.getTime());
                        btnClassEndDate.setText(endDate);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;
            case START_TIME:
                calendar = Calendar.getInstance();
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        startTime = format(new Locale("zh", "HK"), "%02d:%02d", hourOfDay, minute);
                        btnClassStartTime.setText(startTime);

                        calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(etClassHour.getText().toString()));
                        endTime = String.format(new Locale("zh", "HK"), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                        btnClassEndTime.setText(endTime);

                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                break;
        }
    }
}
