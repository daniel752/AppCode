package com.bankingapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bankingapplication.Adapters.CustomerAdapter;
import com.bankingapplication.Model.Account;
import com.bankingapplication.Model.Clerk;
import com.bankingapplication.Model.Profile;
import com.bankingapplication.Model.User;
import com.bankingapplication.Model.db.ApplicationDB;
import com.example.mikebanks.bankscorpfinancial.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CustomerOverviewFragment extends Fragment
{
	private FloatingActionButton fab;
	private ListView lstProfiles;
	private TextView txtTitle;
	private TextView txtCustomerFirstName;
	private TextView txtCustomerLastName;
	private TextView txtCustomerCountry;
	private TextView txtCustomerUsername;
	private TextView txtCustomerAccountNum;
	private TextView txtTitleAccount;
	private EditText edtAccountName;
	private EditText edtAccountInitAmount;
	//private Button btnAddClerk;
	//private Dialog clerkDialog;
	private int selectedProfileIndex;
	private Gson gson;
	private User user;
	private SharedPreferences userPreferences;
	private ArrayList<Profile> customers;
	private ArrayAdapter<Profile> customerAdapter;
	private Dialog customerDialog;
	private Button btnSuccess;
	private Button btnCancel;
	private Dialog accountDialog;

	public CustomerOverviewFragment()
	{
		//Has to be empty
	}

	public View.OnClickListener customerDialogClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if(view.getId() == R.id.btn_success_customer_dialog)
			{
				displayAccountDialog();
			}
			else if(view.getId() == R.id.btn_cancel_customer_dialog)
			{
				customerDialog.dismiss();
			}
		}
	};

	public View.OnClickListener accountDialogClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if(view.getId() == R.id.btn_add_account_dlg)
			{
				openAccount();
			}
			else if(view.getId() == R.id.btn_cancel_account_dlg)
			{
				accountDialog.dismiss();
			}
		}
	};

	private void displayAccountDialog()
	{
		accountDialog = new Dialog(getActivity());
		accountDialog.setContentView(R.layout.account_dialog);
		accountDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		accountDialog.setCanceledOnTouchOutside(true);
		accountDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialogInterface)
			{
				Toast.makeText(getActivity(), "Account creation canceled", Toast.LENGTH_SHORT).show();
			}
		});
		txtTitleAccount = accountDialog.findViewById(R.id.txt_title_account_dialog);
		edtAccountName = accountDialog.findViewById(R.id.edt_account_name);
		edtAccountInitAmount = accountDialog.findViewById(R.id.edt_init_balance);
		btnSuccess = accountDialog.findViewById(R.id.btn_add_account_dlg);
		btnCancel = accountDialog.findViewById(R.id.btn_cancel_account_dlg);
		btnSuccess.setOnClickListener(accountDialogClickListener);
		btnCancel.setOnClickListener(accountDialogClickListener);
		accountDialog.show();
	}

	private void openAccount()
	{
		String accountName = accountDialog.findViewById(R.id.edt_account_name).toString();
		//String accountNum = "A" + (customerAdapter.getItem(selectedProfileIndex).getAccounts().size() + 1);
		double accountInitBalance = Double.parseDouble(accountDialog.findViewById(R.id.edt_init_balance).toString());
		customerAdapter.getItem(selectedProfileIndex).addAccount(accountName,accountInitBalance);
		//ApplicationDB applicationDB = new ApplicationDB(getActivity().getApplicationContext());
		//applicationDB.saveNewAccount(customerAdapter.getItem(selectedProfileIndex),new Account(accountName,accountNum,accountInitBalance));
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		customers = new ArrayList<>();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_profile_overview, container, false);
		lstProfiles = rootView.findViewById(R.id.lst_profiles_overview);
		txtTitle = rootView.findViewById(R.id.txt_profile_fragment_title);
		getActivity().setTitle("My Customers");
		((DrawerActivity) getActivity()).showDrawerButton();
		setValues();
		return rootView;
	}

	private void displayCustomerDialog(int index)
	{
		customerDialog = new Dialog(getActivity());
		customerDialog.setContentView(R.layout.customer_dialog);
		customerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		customerDialog.setCanceledOnTouchOutside(true);
		customerDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialogInterface)
			{
				Toast.makeText(getActivity(), "Customer's info closed", Toast.LENGTH_SHORT).show();
			}
		});
		txtCustomerFirstName = customerDialog.findViewById(R.id.txt_fname_customer_dialog);
		txtCustomerLastName = customerDialog.findViewById(R.id.txt_lname_customer_dialog);
		txtCustomerCountry = customerDialog.findViewById(R.id.txt_country_customer_dialog);
		txtCustomerUsername = customerDialog.findViewById(R.id.txt_username_customer_dialog);
		//txtCustomerAccountNum = customerDialog.findViewById(R.id.txt_accounts_num_customer_dialog);
		txtCustomerFirstName.setText(customerAdapter.getItem(index).getFirstName());
		txtCustomerLastName.setText(customerAdapter.getItem(index).getLastName());
		txtCustomerCountry.setText(customerAdapter.getItem(index).getCountry());
		txtCustomerUsername.setText(customerAdapter.getItem(index).getUsername());
		//txtCustomerAccountNum.setText(customerAdapter.getItem(index).getNumberOfAccounts());
		btnCancel = customerDialog.findViewById(R.id.btn_cancel_customer_dialog);
		btnSuccess = customerDialog.findViewById(R.id.btn_success_customer_dialog);
		btnCancel.setOnClickListener(customerDialogClickListener);
		btnSuccess.setOnClickListener(customerDialogClickListener);
		customerDialog.show();
	}

	private void setValues()
	{
		selectedProfileIndex = 0;
		String json = "";
		ApplicationDB applicationDB = new ApplicationDB(getActivity().getApplicationContext());
		//clerks = applicationDB.getAllClerks();
		userPreferences = this.getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
		gson = new Gson();
		json = userPreferences.getString("clerkUser", "");
		user = gson.fromJson(json, Clerk.class);
//			if (user instanceof Clerk)
//			{
//				profileAdapter = new ProfileAdapter(this.getActivity(), R.layout.lst_profiles, ((Clerk) user).getUsers());
//			}
		customerAdapter = new CustomerAdapter(this.getActivity(),R.layout.lst_profile_row,applicationDB.getClerkCustomers(applicationDB.getDbIdByUsername(user.getUsername())));
//		fab.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				displayAddClerkDialog();
//			}
//		});
		lstProfiles.setAdapter(customerAdapter);
		lstProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int i, long id)
			{
				selectedProfileIndex = i;
				displayCustomerDialog(i);
			}
		});
	}
}
