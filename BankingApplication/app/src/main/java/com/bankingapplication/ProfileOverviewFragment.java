package com.bankingapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bankingapplication.Adapters.ProfileAdapter;
import com.bankingapplication.Model.Admin;
import com.bankingapplication.Model.Clerk;
import com.bankingapplication.Model.Profile;
import com.bankingapplication.Model.User;
import com.bankingapplication.Model.db.ApplicationDB;
import com.example.mikebanks.bankscorpfinancial.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ProfileOverviewFragment extends Fragment
{
	private FloatingActionButton fab;
	private ListView lstProfiles;
	private TextView txtTitle;
	private TextView txtProfileFirstName;
	private TextView txtProfileLastName;
	private TextView txtProfileCountry;
	private TextView txtProfileUsername;
	private Button btnCancel;
	//private Button btnAddClerk;
	//private Dialog clerkDialog;
	private int selectedProfileIndex;
	private Gson gson;
	private User user;
	private SharedPreferences userPreferences;
	private ArrayList<Profile> profiles;
	private ArrayAdapter<Profile> profileAdapter;
	private Dialog profileDialog;
	private Button btnAssign;
	//private boolean displayClerkDialogOnLaunch;


	public ProfileOverviewFragment()
	{
		//Needs to be an empty constructor
	}

	private View.OnClickListener assignUserClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if (view.getId() == btnCancel.getId())
			{
				profileDialog.dismiss();
				Toast.makeText(getActivity(), "User Assign Canceled", Toast.LENGTH_SHORT).show();
			}
			else if (view.getId() == btnAssign.getId())
			{
				assignUserToClerk();
			}
		}
	};

	private void assignUserToClerk()
	{
		if(user instanceof Clerk)
		{
			((Clerk) user).assignProfileToCustomer(profileAdapter.getItem(selectedProfileIndex),getContext());
			Toast.makeText(getActivity(),"User has been assigned to you",Toast.LENGTH_SHORT).show();
			profileDialog.dismiss();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		profiles = new ArrayList<>();
//		displayClerkDialogOnLaunch = false;
//
//		if (bundle != null) {
//			displayClerkDialogOnLaunch = bundle.getBoolean("DisplayClerkDialog", false);
//		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_profile_overview, container, false);
//		fab = rootView.findViewById(R.id.floating_action_btn);
		lstProfiles = rootView.findViewById(R.id.lst_profiles_overview);
		txtTitle = rootView.findViewById(R.id.txt_profile_fragment_title);

		getActivity().setTitle("Users");
		((DrawerActivity) getActivity()).showDrawerButton();

		setValues();

//		if (displayClerkDialogOnLaunch)
//		{
//			displayAddClerkDialog();
//			displayClerkDialogOnLaunch = false;
//		}
		return rootView;
	}

	private void displayProfileDialog(int index)
	{
		profileDialog = new Dialog(getActivity());
		profileDialog.setContentView(R.layout.profile_dialog);
		profileDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		profileDialog.setCanceledOnTouchOutside(true);
		profileDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialogInterface)
			{
				Toast.makeText(getActivity(), "User assign canceled", Toast.LENGTH_SHORT).show();
			}
		});
		txtProfileFirstName = profileDialog.findViewById(R.id.txt_first_name_profile_dialog);
		txtProfileLastName = profileDialog.findViewById(R.id.txt_last_name_profile_dialog);
		txtProfileCountry = profileDialog.findViewById(R.id.txt_country_profile_dialog);
		txtProfileUsername = profileDialog.findViewById(R.id.txt_username_profile_dialog);
		txtProfileFirstName.setText(profileAdapter.getItem(index).getFirstName());
		txtProfileLastName.setText(profileAdapter.getItem(index).getLastName());
		txtProfileCountry.setText(profileAdapter.getItem(index).getCountry());
		txtProfileUsername.setText(profileAdapter.getItem(index).getUsername());
		btnCancel = profileDialog.findViewById(R.id.btn_cancel_profile_dialog);
		btnAssign = profileDialog.findViewById(R.id.btn_assign_profile_dialog);
		btnCancel.setOnClickListener(assignUserClickListener);
		btnAssign.setOnClickListener(assignUserClickListener);
		profileDialog.show();
	}

	private void setValues()
	{
		selectedProfileIndex = 0;
		String json = "";
		ApplicationDB applicationDB = new ApplicationDB(getActivity().getApplicationContext());
		//clerks = applicationDB.getAllClerks();
		userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
		gson = new Gson();
		if(LoginFragment.getUserRole() == User.USER_TYPE.ADMIN.getValue())
		{
			json = userPreferences.getString("adminUser", "");
			user = gson.fromJson(json, Admin.class);
//			profileAdapter = new ProfileAdapter(this.getActivity(),R.layout.lst_profile_row,applicationDB.getAllProfiles());
		}
		else if(LoginFragment.getUserRole() == User.USER_TYPE.CLERK.getValue())
		{
			json = userPreferences.getString("clerkUser", "");
			user = gson.fromJson(json, Clerk.class);
//			if (user instanceof Clerk)
//			{
//				profileAdapter = new ProfileAdapter(this.getActivity(),R.layout.lst_profile_row,applicationDB.getAllProfiles());
//			}
		}
		profileAdapter = new ProfileAdapter(this.getActivity(),R.layout.lst_profile_row,applicationDB.getAllProfiles());
//		fab.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				displayAddClerkDialog();
//			}
//		});
		lstProfiles.setAdapter(profileAdapter);
		lstProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int i, long id)
			{
				selectedProfileIndex = i;
				if(user instanceof Clerk)
				{
					displayProfileDialog(i);
				}
				else if(user instanceof Admin)
				{
					//Todo: implement user info dialog for admin
				}
			}
		});
	}

	private void viewUser()
	{

	}
}
