package com.example.emotionapp.fragment;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;
import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.emotionapp.R;
import com.example.emotionapp.SharedPreferencesHelper;
import com.example.emotionapp.activity.MainActivity;
import com.example.emotionapp.auth.LoginActivity;
import com.example.emotionapp.databinding.FragmentMainSettingsBinding;
import com.example.emotionapp.notifications.AlarmBroadcast;
import com.example.emotionapp.notifications.ScheduleModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainSettingsFragment extends Fragment {
    FragmentMainSettingsBinding binding;
    SharedPreferencesHelper helper;
    private Calendar calendar;
    MainActivity activity;


    public MainSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity= (MainActivity) requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        helper = new SharedPreferencesHelper(activity);
        binding.tvDarkMode.setChecked(helper.isDarkMode());
        binding.tvReminder.setChecked(helper.getDetails() != null);

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(activity, LoginActivity.class));
                requireActivity().finish();
            }
        });

        binding.tvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(MainSettingsFragment.this).navigate(R.id.action_mainSettingsFragment_to_settingsFragment);
            }
        });

        binding.tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
            }
        });

        binding.tvDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                helper.setDarkMode(b);
                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }
        });
        binding.cvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("data", true);
                findNavController(MainSettingsFragment.this).navigate(R.id.action_mainSettingsFragment_to_policyFragment, bundle);
            }
        });
        binding.cvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("data", false);
                findNavController(MainSettingsFragment.this).navigate(R.id.action_mainSettingsFragment_to_policyFragment, bundle);
            }
        });

        binding.cvReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReminder();
            }
        });
        binding.tvReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    requestNotificationPermissionIfNecessary(requireActivity());
                    setReminder();
                } else {
                    cancelAlarm();
                }
            }
        });
    }

    private void setReminder() {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_reminder, null, false);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity);
        alertDialogBuilder.setView(view);
        EditText editText = view.findViewById(R.id.etDate);
        EditText etDetails = view.findViewById(R.id.etDetails);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(editText);
            }
        });

        alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String time = editText.getText().toString();
                String details = etDetails.getText().toString();
                if (time.isEmpty()) {
                    Toast.makeText(activity, "Select Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (details.isEmpty()) {
                    Toast.makeText(activity, "Enter Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                ScheduleModel scheduleModel = new ScheduleModel(details, calendar.getTimeInMillis());
                setAlarm(scheduleModel);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDateTimePicker(EditText editText) {
        calendar = Calendar.getInstance();
        // Get Current Date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimePicker(editText);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(EditText editText) {
        // Get Current Time
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Update EditText with selected date and time
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        editText.setText(sdf.format(calendar.getTime()));

                        // Get time in milliseconds
                        long timeInMillis = calendar.getTimeInMillis();
                        // Do whatever you want with the timeInMillis here
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void setAlarm(ScheduleModel schedule) {
        AlarmManager am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(requireActivity(), AlarmBroadcast.class);

        intent.putExtra("data", schedule.getDetails());
        intent.putExtra("time",schedule.getTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms()) {
                am.setExact(AlarmManager.RTC_WAKEUP, schedule.getTime(), pendingIntent);
            } else {
                startActivity(new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM));

            }
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, schedule.getTime(), pendingIntent);

        }
        helper.setDetails(schedule.getDetails());
        //   Toast.makeText(activity, "Schedule Added", Toast.LENGTH_SHORT).show();

    }

    private void cancelAlarm() {
        AlarmManager am = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(activity, AlarmBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Cancel the alarm
        am.cancel(pendingIntent);
        helper.setDetails(null);
        Toast.makeText(activity, "Alarm canceled successfully!", Toast.LENGTH_SHORT).show();
    }

    public boolean requestNotificationPermissionIfNecessary(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
