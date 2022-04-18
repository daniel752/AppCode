package com.bankingapplication;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bankingapplication.Adapters.ClerkAdapter;
import com.bankingapplication.Model.Admin;
import com.bankingapplication.Model.Clerk;
import com.bankingapplication.Model.db.ApplicationDB;
import com.example.mikebanks.bankscorpfinancial.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ClerkOverviewFragment extends Fragment
{
	private FloatingActionButton fab;
	private ListView lstClerks;
	private TextView txtTitle;
	private EditText edtClerkFirstName;
	private EditText edtClerkLastName;
	private EditText edtClerkCountry;
	private EditText edtClerkUsername;
	private EditText edtClerkPassword;
	private EditText edtClerkPasswordConfirm;
	private Button btnCancel;
	private Button btnAddClerk;
	private Dialog clerkDialog;
	private int selectedClerkIndex;
	private Gson gson;
	private Admin userAdmin;
	private SharedPreferences userPreferences;
	private ArrayList<Clerk> clerks;

	private boolean displayClerkDialogOnLaunch;

	public ClerkOverviewFragment()
	{
		//Needs to be an empty constructor
	}

	private View.OnClickListener addAccountClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if (view.getId() == btnCancel.getId())
			{
				clerkDialog.dismiss();
				Toast.makeText(getActivity(), "Clerk Creation Cancelled", Toast.LENGTH_SHORT).show();
			}
			else if (view.getId() == btnAddClerk.getId())
			{
				if(validateClerkInfo())
				{
					addClerk();
					clerkDialog.dismiss();
					ClerkOverviewFragment clerkOverviewFragment = new ClerkOverviewFragment();
					getActivity().getSupportFragmentManager().beginTransaction()
							.replace(R.id.flContent, clerkOverviewFragment,"findThisFragment")
							.addToBackStack(null)
							.commit();
					Toast.makeText(getActivity(),"Clerk has been added to the system",Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(getActivity(),"Check Clerk info and try again",Toast.LENGTH_SHORT).show();
			}
		}
	};

	private boolean validateClerkInfo()
	{
		return !edtClerkFirstName.getText().toString().equals(null)&&
		       !edtClerkLastName.getText().toString().equals(null)&&
		       !edtClerkCountry.getText().toString().equals(null)&&
		       !edtClerkUsername.getText().toString().equals(null)&&
		       !edtClerkPassword.getText().toString().equals(null)&&
		       !edtClerkPasswordConfirm.getText().toString().equals(null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		clerks = new ArrayList<>();
		displayClerkDialogOnLaunch = false;

		if (bundle != null) {
			displayClerkDialogOnLaunch = bundle.getBoolean("DisplayClerkDialog", false);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_clerk_overview, container, false);

		fab = rootView.findViewById(R.id.floating_action_btn);

		lstClerks = rootView.findViewById(R.id.lst_clerks);
		txtTitle = rootView.findViewById(R.id.txt_clerk_fragment_title);

		getActivity().setTitle("Clerks");
		((DrawerActivity) getActivity()).showDrawerButton();

		setValues();

		if (displayClerkDialogOnLaunch)
		{
			displayAddClerkDialog();
			displayClerkDialogOnLaunch = false;
		}
		return rootView;
	}

	private void displayAddClerkDialog()
	{
		clerkDialog = new Dialog(getActivity());
		clerkDialog.setContentView(R.layout.add_clerk_dialog);

		clerkDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		clerkDialog.setCanceledOnTouchOutside(true);
		clerkDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialogInterface)
			{
				Toast.makeText(getActivity(), "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
			}
		});

		edtClerkFirstName = clerkDialog.findViewById(R.id.edt_clerk_first_name);
		edtClerkLastName = clerkDialog.findViewById(R.id.edt_clerk_last_name);
		edtClerkCountry = clerkDialog.findViewById(R.id.edt_clerk_country);
		edtClerkUsername = clerkDialog.findViewById(R.id.edt_clerk_username);
		edtClerkPassword = clerkDialog.findViewById(R.id.edt_clerk_password);
		edtClerkPasswordConfirm = clerkDialog.findViewById(R.id.edt_clerk_password_confirm);
		btnCancel = clerkDialog.findViewById(R.id.btn_cancel_add_clerk_dialog);
		btnAddClerk = clerkDialog.findViewById(R.id.btn_add_clerk_dialog);
		btnCancel.setOnClickListener(addAccountClickListener);
		btnAddClerk.setOnClickListener(addAccountClickListener);

		clerkDialog.show();
	}

	private void setValues()
	{
		selectedClerkIndex = 0;
		ApplicationDB applicationDB = new ApplicationDB(getActivity().getApplicationContext());
		//clerks = applicationDB.getAllClerks();
		userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
		gson = new Gson();
		String json = userPreferences.getString("adminUser", "");
		userAdmin = gson.fromJson(json, Admin.class);

		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				displayAddClerkDialog();
			}
		});

		ClerkAdapter clerkAdapter = new ClerkAdapter(this.getActivity(),R.layout.lst_profile_row,applicationDB.getAllClerks());
		lstClerks.setAdapter(clerkAdapter);
		lstClerks.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int i, long id)
			{
				selectedClerkIndex = i;
				viewClerk();
			}
		});
	}

	private void addClerk()
	{
		Clerk clerk = new Clerk(
				edtClerkFirstName.getText().toString(),
				edtClerkLastName.getText().toString(),
				edtClerkCountry.getText().toString(),
				edtClerkUsername.getText().toString(),
				edtClerkPassword.getText().toString());
		ApplicationDB applicationDB = new ApplicationDB(getActivity().getApplicationContext());
		applicationDB.saveNewUser(clerk);
		applicationDB.saveNewClerk(clerk);
	}

	private void viewClerk()
	{

	}

}
