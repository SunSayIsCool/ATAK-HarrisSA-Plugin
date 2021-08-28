
package com.atakmap.android.harrissaspr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDown.OnStateListener;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.gui.PluginSpinner;
import com.atakmap.android.harrissaspr.converters.HarrisSAparser;
import com.atakmap.android.harrissaspr.converters.MILSTDconverter;
import com.atakmap.android.harrissaspr.converters.SprSAparser;
import com.atakmap.android.harrissaspr.driver.CdcAcmSerialDriver;
import com.atakmap.android.harrissaspr.driver.ProbeTable;
import com.atakmap.android.harrissaspr.driver.UsbSerialDriver;
import com.atakmap.android.harrissaspr.driver.UsbSerialProber;
import com.atakmap.android.harrissaspr.plugin.R;
import com.atakmap.android.maps.MapView;
import com.atakmap.app.BuildConfig;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.coords.GeoPoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class HarrisSaSprDropDownReceiver extends DropDownReceiver implements
        OnStateListener {

    public static final String TAG = HarrisSaSprDropDownReceiver.class
            .getSimpleName();

    public static final String SHOW_PLUGIN = "com.atakmap.android.harrissaspr.SHOW_PLUGIN";
    private final View templateView;
    private final Context pluginContext;
    private Boolean hhmpSw = false;
    private Boolean sprSw = false;
    private Boolean hhmpGps = false;
    private Boolean sprGps = false;
    private Thread SAThread = null;
    private Thread SerialThread = null;
    private int hhmp_count = 0;
    private int spr_count = 0;
    private Boolean hhmp_spr = true;
    private String harris_spr_settings = Environment.getExternalStorageDirectory().getAbsolutePath() + "/atak/tools/harris_spr_settings.csv";
    private String harris_db = Environment.getExternalStorageDirectory().getAbsolutePath() + "/atak/tools/harris_db.csv";
    private String spr_db = Environment.getExternalStorageDirectory().getAbsolutePath() + "/atak/tools/spr_db.csv";

    BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "spr_service";
    public final static String SPR_BYTE = "spr_byte_array";
    public final static String UDP_STRING = "hhmp_udp_string";
    public final static String PARAM_STATUS = "status";

    static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

    int[] iconsDOMAIN = {R.drawable.air, R.drawable.vehicle, R.drawable.weapon,
            R.drawable.combat, R.drawable.cs, R.drawable.css,
            R.drawable.sea_track, R.drawable.subsurf_, R.drawable.sof};

    int[] iconsAIRMIL = {R.drawable.fixed, R.drawable.fixed_attack, R.drawable.fixed_bomber,
            R.drawable.fixed_transport, R.drawable.fixed_c2, R.drawable.fixed_fighter,
            R.drawable.fixed_interceptor, R.drawable.fixed_csar, R.drawable.fixed_jammer,
            R.drawable.fixed_tanker, R.drawable.fixed_vstol, R.drawable.fixed_sof,
            R.drawable.fixed_medevac, R.drawable.fixed_patrol, R.drawable.fixed_uav,
            R.drawable.fixed_recon, R.drawable.fixed_trainer, R.drawable.fixed_utility,
            R.drawable.fixed_c3i, R.drawable.rotor, R.drawable.rotor_attack,
            R.drawable.rotor_transport, R.drawable.rotor_c2, R.drawable.rotor_csar, R.drawable.rotor_jammer,
            R.drawable.rotor_sof, R.drawable.rotor_medevac, R.drawable.rotor_uav,
            R.drawable.rotor_recon, R.drawable.rotor_utility, R.drawable.blimp};

    int[] iconsVEHICLE = {R.drawable.vehicle, R.drawable.vehicle_armor_gun, R.drawable.vehicle_apc,
            R.drawable.vehicle_apc_recovery, R.drawable.vehicle_c2v_acv, R.drawable.vehicle_armor_infanrty,
            R.drawable.vehicle_armor_light, R.drawable.vehicle_armor_css, R.drawable.vehicle_tank,
            R.drawable.vehicle_civ, R.drawable.vehicle_engineer, R.drawable.vehicle_mcv,
            R.drawable.vehicle_utility, R.drawable.vehicle_bus, R.drawable.vehicle_truck,
            R.drawable.vehicle_boat, R.drawable.vehicle_semi, R.drawable.vehicle_ambulance,};

    int[] iconsWEAPON = {R.drawable.weapon, R.drawable.weapon_air_defense, R.drawable.weapon_direct_fire,
            R.drawable.weapon_anti_tank_gun, R.drawable.weapon_howitzer, R.drawable.weapon_missile_launcher,
            R.drawable.weapon_mortar, R.drawable.weapon_rifle, R.drawable.weapon_automatic,
            R.drawable.weapon_lmg, R.drawable.weapon_hmg, R.drawable.weapon_rocket_launcher_single,
            R.drawable.weapon_rocket_launcher_multiple, R.drawable.weapon_rocket_antitank, R.drawable.weapon_flame,
            R.drawable.weapon_nbc};

    int[] iconsCOMBAT = {R.drawable.combat, R.drawable.combat_anti_armor, R.drawable.combat_aa_armored_tracked,
            R.drawable.combat_aa_armored_air_assault, R.drawable.combat_aa_armored_tracked, R.drawable.combat_aa_armored_wheeled, R.drawable.combat_aa_airborne,
            R.drawable.combat_aa_motorized, R.drawable.combat_armor_track, R.drawable.combat_armor_track_amphibious,
            R.drawable.combat_armor_wheeled, R.drawable.combat_armor_wheeled_airborne, R.drawable.combat_armor_wheeled_recovery,
            R.drawable.combat_armor_wheeled_amphibious, R.drawable.weapon_air_defense, R.drawable.combat_air_defense_missile,
            R.drawable.combat_engineer, R.drawable.combat_artillery_fixed, R.drawable.combat_mortar,
            R.drawable.combat_rocket, R.drawable.combat_single_rocket_self, R.drawable.combat_multi_rocket_self,
            R.drawable.combat_inf_troops, R.drawable.combat_inf_airborne, R.drawable.combat_inf_ifv,
            R.drawable.combat_inf_motorized, R.drawable.combat_inf_naval, R.drawable.combat_inf_air_assault,
            R.drawable.combat_inf_mountain, R.drawable.combat_inf_mechanized, R.drawable.comabat_missile_ss,
            R.drawable.comabat_recon, R.drawable.comabat_recon_airborne, R.drawable.comabat_recon_mountain,
            R.drawable.comabat_recon_marine, R.drawable.comabat_recon_air_assault, R.drawable.comabat_isf,
            R.drawable.comabat_aviation, R.drawable.comabat_composite, R.drawable.comabat_fixed_wing,
            R.drawable.comabat_rotary_wing,};

    int[] iconsCS = {R.drawable.cs, R.drawable.cs_nbc, R.drawable.cs_biological, R.drawable.cs_chemical,
            R.drawable.cs_nuclear, R.drawable.cs_eod, R.drawable.cs_iw,
            R.drawable.cs_law_enforcement, R.drawable.cs_civ_law_enforcement, R.drawable.cs_mp,
            R.drawable.cs_mi, R.drawable.cs_ci, R.drawable.cs_sigint,
            R.drawable.cs_ew, R.drawable.cs_signal, R.drawable.cs_com_ops,
            R.drawable.cs_sig_radio, R.drawable.cs_sig_sat, R.drawable.cs_sig_relay,
            R.drawable.cs_sig_tel_switch};

    int[] iconsCSS = {R.drawable.c2_hq, R.drawable.css_med, R.drawable.css_supply,
            R.drawable.css_transport, R.drawable.css_maintenance, R.drawable.css_recovery};

    int[] iconsSEA = {R.drawable.sea_track, R.drawable.sea_combatant, R.drawable.sea_amphibious,
            R.drawable.sea_assault_vessel, R.drawable.sea_landing_craft, R.drawable.sea_landing_ship,
            R.drawable.sea_hovercraft, R.drawable.sea_line, R.drawable.sea_battleship,
            R.drawable.sea_cruiser, R.drawable.sea_carrier, R.drawable.sea_destroyer,
            R.drawable.sea_frigate, R.drawable.sea_mine_warfare, R.drawable.sea_minehunter,
            R.drawable.sea_minelayer, R.drawable.sea_patrol, R.drawable.sea_antisubmarine,
            R.drawable.sea_antisurface, R.drawable.sea_convoy, R.drawable.sea_hospital,
            R.drawable.sea_rescue, R.drawable.sea_non_mil, R.drawable.sea_cargo,
            R.drawable.sea_passenger, R.drawable.sea_tanker};

    int[] iconsSUBSURFACE = {R.drawable.subsurf_, R.drawable.subsurf_diver, R.drawable.subsurf_submarine,
            R.drawable.subsurf_submarine_conv, R.drawable.subsurf_submarine_nuc, R.drawable.subsurf_uuv};

    int[] iconsSOF = {R.drawable.sof_fixed, R.drawable.sof_rotary, R.drawable.sof_csar,
            R.drawable.sof_naval, R.drawable.sof_boat, R.drawable.sof_ssnr,
            R.drawable.sof_ground, R.drawable.sof_ranger, R.drawable.sof_psyops,
            R.drawable.sof_support};

    int share_img = R.drawable.share;


    /**************************** CONSTRUCTOR *****************************/


    public HarrisSaSprDropDownReceiver(final MapView mapView, final Context context) {
        super(mapView);
        this.pluginContext = context;

        ProbeTable customTable = new ProbeTable();
        customTable.addProduct(0x19a5, 0x0012, CdcAcmSerialDriver.class);
        final UsbSerialProber prober = new UsbSerialProber(customTable);
        final UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        final Intent startHhmpServiceIntent = new Intent("com.atakmap.android.harrissaspr.HhmpUdpService");
        startHhmpServiceIntent.setPackage("com.atakmap.android.harrissaspr.plugin");
        final Intent startSprServiceIntent = new Intent("com.atakmap.android.harrissaspr.SprSerialService");
        startSprServiceIntent.setPackage("com.atakmap.android.harrissaspr.plugin");

        // Remember to use the PluginLayoutInflator if you are actually inflating a custom view
        // In this case, using it is not necessary - but I am putting it here to remind
        // developers to look at this Inflator
        templateView = PluginLayoutInflater.inflate(context, R.layout.main_layout, null);
        TabHost tabHost = templateView.findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tbHHMP);
        tabSpec.setIndicator("7800/7850 HH/MP");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tbSPR);
        tabSpec.setIndicator("7800S");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            // Get the title
            TextView tabs_title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            // Define the size of the font
            tabs_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.tab_text_size));
            // Define alignment
            tabs_title.setGravity(Gravity.CENTER);
        }

        final Switch hhmpSwitch = templateView.findViewById(R.id.swBFTHHMP);
        final Switch sprSwitch = templateView.findViewById(R.id.swBFTSPR);
        Switch hhmpGpsSwitch = templateView.findViewById(R.id.swGPSHHMP);
        Switch sprGpsSwitch = templateView.findViewById(R.id.swGPSSPR);
        final Button addBtn = templateView.findViewById(R.id.btnAdd);
        final Button saveBtn = templateView.findViewById(R.id.btnSave);
        final TableLayout tblLayoutHHMP = templateView.findViewById(R.id.tblLayout_hhmp);
        final TableLayout tblLayoutSPR = templateView.findViewById(R.id.tblLayout_spr);
        final EditText HHMP_GPS_ID = templateView.findViewById(R.id.edttxtSelfCombatIDHHMP);
        final EditText SPR_GPS_ID = templateView.findViewById(R.id.edttxtSelfCombatIDSPR);

        // Init settings and SA Tables
        init(context);

        hhmpGpsSwitch.setClickable(false);
        sprGpsSwitch.setClickable(false);
        // TabChange Listener
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "tag1":
                        hhmp_spr = true;
                        break;
                    case "tag2":
                        hhmp_spr = false;
                        break;
                }
            }
        });

        // HH/MP BFT switch change code
        hhmpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hhmpSw = true;
                    hhmpGpsSwitch.setClickable(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        getMapView().getContext().startForegroundService(startHhmpServiceIntent);
                    else
                        getMapView().getContext().startService(startHhmpServiceIntent);
                    Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.HHMP_BFT_ENABLED), Toast.LENGTH_SHORT).show();
                } else {
                    getMapView().getContext().stopService(startHhmpServiceIntent);
                    hhmpSw = false;
                    hhmpGpsSwitch.setClickable(false);
                    hhmpGpsSwitch.setChecked(false);
                    Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.HHMP_BFT_DISABLED), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // HH/MP use radio GPS as source
        hhmpGpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (HHMP_GPS_ID.getText().toString().equals("")) {
                        hhmpGpsSwitch.setChecked(false);
                        Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.RadioIDempty), Toast.LENGTH_SHORT).show();
                    } else {
                        hhmpGps = true;
                        Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.HHMP_GPS_ENABLED), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hhmpGps = false;
                    Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.HHMP_BFT_DISABLED), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // SPR BFT switch change code
        sprSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<UsbSerialDriver> drivers = prober.findAllDrivers(manager);

                if (isChecked == true) {
                    if (drivers.isEmpty()) {
                        sprSwitch.setChecked(false);
                        Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.SPR_radio_not_found), Toast.LENGTH_SHORT).show();
                    } else {
                        UsbSerialDriver driver = drivers.get(0);
                        sprSw = true;
                        sprGpsSwitch.setClickable(true);
                        //TODO Start spr serial service

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            getMapView().getContext().startForegroundService(startSprServiceIntent);
                        else
                            getMapView().getContext().startService(startSprServiceIntent);
                        Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.SPR_BFT_ENABLED), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    getMapView().getContext().stopService(startSprServiceIntent);
                    sprSw = false;
                    sprGpsSwitch.setChecked(false);
                    sprGpsSwitch.setClickable(false);
                    Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.SPR_BFT_DISABLED), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // SPR use radio GPS as source
        sprGpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<UsbSerialDriver> drivers = prober.findAllDrivers(manager);
                if (isChecked) {
                    if (SPR_GPS_ID.getText().toString().equals("")) {
                        sprGpsSwitch.setChecked(false);
                        Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.RadioIDempty), Toast.LENGTH_SHORT).show();
                    } else if (drivers.isEmpty()) {
                        sprGpsSwitch.setChecked(false);
                        Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.SPR_radio_not_found), Toast.LENGTH_SHORT).show();
                    } else {
                        sprGps = true;
                        Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.SPR_GPS_ENABLED), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    sprGps = false;
                    Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.SPR_GPS_DISABLED), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add button onClick code
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TableLayout t1;
                final String tblTag;
                final int count;
                if (hhmp_spr) {
                    t1 = templateView.findViewById(R.id.tblLayout_hhmp);
                    hhmp_count++;
                    count = hhmp_count;
                    tblTag = "HHMP";
                } else {
                    t1 = templateView.findViewById(R.id.tblLayout_spr);
                    spr_count++;
                    count = spr_count;
                    tblTag = "SPR";
                }

                final TableRow tr_head = new TableRow(context);
                tr_head.setTag("Table" + tblTag + count);
                tr_head.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 100));

                CheckBox chkShare = new CheckBox(context);
                chkShare.setTag("chkShare" + tblTag + count);
                chkShare.setPadding(0, 0, 0, 0);
                chkShare.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 100));
                tr_head.addView(chkShare);

                ImageView chkImg = new ImageView(context);
                chkImg.setImageResource(share_img);
                chkImg.setLayoutParams(new TableRow.LayoutParams(50, 110));
                chkImg.setPadding(0, 0, 0, 0);
                chkImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                tr_head.addView(chkImg);

                EditText edComabatID = new EditText(context);
                edComabatID.setTag("CombatID" + tblTag + count);
                edComabatID.setHint(context.getResources().getString(R.string.COMBAT_ID));
                edComabatID.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.spinner_item));
                edComabatID.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                edComabatID.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 110, 1));
                tr_head.addView(edComabatID);

                EditText edAlias = new EditText(context);
                edAlias.setTag("Alias" + tblTag + count);
                edAlias.setHint(context.getResources().getString(R.string.MAP_ALIAS));
                edAlias.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.spinner_item));
                edAlias.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                edAlias.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 110, 1));
                tr_head.addView(edAlias);

                Spinner spnDomain = new PluginSpinner(context);
                spnDomain.setTag("spnDomain" + tblTag + count);
                String[] domain_str = context.getResources().getStringArray(R.array.domain_list);
                IconsAdapter domain_adapter = new IconsAdapter(context, iconsDOMAIN, domain_str);
                spnDomain.setAdapter(domain_adapter);
                spnDomain.setBackgroundResource(R.drawable.new_dark_button_bg);
                spnDomain.setPrompt(context.getResources().getString(R.string.CHOOSE_DOMAIN));
                spnDomain.setLayoutParams(new TableRow.LayoutParams(100, 100));
                spnDomain.setPadding(0, 0, 0, 0);
                tr_head.addView(spnDomain);

                final Spinner spnUnit = new PluginSpinner(context);
                spnUnit.setBackgroundResource(R.drawable.new_dark_button_bg);
                spnUnit.setTag("spnUnit" + tblTag + count);
                String[] units = context.getResources().getStringArray(R.array.air_mil_list);
                IconsAdapter air = new IconsAdapter(context, iconsAIRMIL, units);
                spnUnit.setAdapter(air);
                spnUnit.setPrompt(context.getResources().getString(R.string.CHOOSE_MARKER));
                spnUnit.setLayoutParams(new TableRow.LayoutParams(100, 100));
                spnUnit.setPadding(0, 0, 0, 0);
                tr_head.addView(spnUnit);

                ImageButton btnRemove = new ImageButton(context);
                btnRemove.setTag("btnRemove" + tblTag + count);
                btnRemove.setImageResource(android.R.drawable.ic_menu_delete);
                btnRemove.setBackgroundResource(R.drawable.new_dark_button_bg);
                btnRemove.setPadding(0, 0, 0, 0);
                btnRemove.setLayoutParams(new TableRow.LayoutParams(100, 100));
                tr_head.addView(btnRemove);

                spnDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                //ArrayAdapter<?> air = ArrayAdapter.createFromResource(context,R.array.air_mil_list, android.R.layout.simple_spinner_item);
                                //air.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                String[] units = context.getResources().getStringArray(R.array.air_mil_list);
                                IconsAdapter air = new IconsAdapter(context, iconsAIRMIL, units);
                                spnUnit.setAdapter(air);
                                break;
                            case 1:
                                String[] vechs = context.getResources().getStringArray(R.array.gnd_vech_list);
                                IconsAdapter vech = new IconsAdapter(context, iconsVEHICLE, vechs);
                                spnUnit.setAdapter(vech);
                                break;
                            case 2:
                                String[] weapons = context.getResources().getStringArray(R.array.gnd_weap_list);
                                IconsAdapter weapon = new IconsAdapter(context, iconsWEAPON, weapons);
                                spnUnit.setAdapter(weapon);
                                break;
                            case 3:
                                String[] cbts = context.getResources().getStringArray(R.array.gnd_unt_cbt_list);
                                IconsAdapter unit_cbt = new IconsAdapter(context, iconsCOMBAT, cbts);
                                spnUnit.setAdapter(unit_cbt);
                                break;
                            case 4:
                                String[] cs = context.getResources().getStringArray(R.array.gnd_unt_cs_list);
                                IconsAdapter unit_cs = new IconsAdapter(context, iconsCS, cs);
                                spnUnit.setAdapter(unit_cs);
                                break;
                            case 5:
                                String[] css = context.getResources().getStringArray(R.array.gnd_unt_ss_list);
                                IconsAdapter unit_css = new IconsAdapter(context, iconsCSS, css);
                                spnUnit.setAdapter(unit_css);
                                break;
                            case 6:
                                String[] surf = context.getResources().getStringArray(R.array.sfc_list);
                                IconsAdapter surface = new IconsAdapter(context, iconsSEA, surf);
                                spnUnit.setAdapter(surface);
                                break;
                            case 7:
                                String[] subsurf = context.getResources().getStringArray(R.array.sub_sfc_list);
                                IconsAdapter subsurface = new IconsAdapter(context, iconsSUBSURFACE, subsurf);
                                spnUnit.setAdapter(subsurface);
                                break;
                            case 8:
                                String[] sof_ = context.getResources().getStringArray(R.array.sof_list);
                                IconsAdapter sof = new IconsAdapter(context, iconsSOF, sof_);
                                spnUnit.setAdapter(sof);
                                break;
                        }
                        return;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        return;
                    }
                });

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t1.removeView(tr_head);
                    }
                });

                t1.addView(tr_head, new TableLayout.LayoutParams());

            }
        });

        // Save button onClick code
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText hhmp_self = templateView.findViewById(R.id.edttxtSelfCombatIDHHMP);
                EditText spr_self = templateView.findViewById(R.id.edttxtSelfCombatIDSPR);

                Writer writer_settings = null;
                // Setting saving loop
                try {
                    File file = new File(harris_spr_settings);

                    writer_settings = new BufferedWriter(new FileWriter(file));

                    String text = hhmp_self.getText().toString() + ";" + spr_self.getText().toString() + "\n";
                    writer_settings.write(text);

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        writer_settings.close();
                        writer_settings.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // HHMP saving loop
                Writer writer_harris_db = null;
                try {
                    File harris_db_file = new File(harris_db);
                    writer_harris_db = new BufferedWriter(new FileWriter(harris_db_file));

                    int row_count = tblLayoutHHMP.getChildCount();
                    if (row_count != 0) {
                        for (int i = 0; i < row_count; i++) {
                            TableRow hhmp_row = (TableRow) tblLayoutHHMP.getChildAt(i);

                            CheckBox chk_share = (CheckBox) hhmp_row.getChildAt(0);
                            EditText edt_combatid = (EditText) hhmp_row.getChildAt(2);
                            EditText edt_alias = (EditText) hhmp_row.getChildAt(3);
                            PluginSpinner spn_domain = (PluginSpinner) hhmp_row.getChildAt(4);
                            PluginSpinner spn_unit = (PluginSpinner) hhmp_row.getChildAt(5);

                            Boolean share = chk_share.isChecked();
                            String combatid = edt_combatid.getText().toString();
                            String alias = edt_alias.getText().toString();
                            int domain = spn_domain.getSelectedItemPosition();
                            int unit = spn_unit.getSelectedItemPosition();

                            if (!combatid.equals("")) {
                                writer_harris_db.write(share.toString() + ";" + combatid + ";" + alias + ";" + domain + ";" + unit + "\n");
                            }


                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        writer_harris_db.close();
                        writer_harris_db.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // SPR saving loop
                Writer writer_srp_db = null;
                try {
                    File spr_db_file = new File(spr_db);
                    writer_srp_db = new BufferedWriter(new FileWriter(spr_db_file));
                    int row_count = tblLayoutSPR.getChildCount();
                    if (row_count != 0) {
                        for (int i = 0; i < row_count; i++) {
                            TableRow spr_row = (TableRow) tblLayoutSPR.getChildAt(i);

                            CheckBox chk_share = (CheckBox) spr_row.getChildAt(0);
                            EditText edt_combatid = (EditText) spr_row.getChildAt(2);
                            EditText edt_alias = (EditText) spr_row.getChildAt(3);
                            PluginSpinner spn_domain = (PluginSpinner) spr_row.getChildAt(4);
                            PluginSpinner spn_unit = (PluginSpinner) spr_row.getChildAt(5);

                            Boolean share = chk_share.isChecked();
                            String combatid = edt_combatid.getText().toString();
                            String alias = edt_alias.getText().toString();
                            int domain = spn_domain.getSelectedItemPosition();
                            int unit = spn_unit.getSelectedItemPosition();


                            if (!combatid.equals("")) {
                                writer_srp_db.write(share.toString() + ";" + combatid + ";" + alias + ";" + domain + ";" + unit + "\n");
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        writer_srp_db.close();
                        writer_srp_db.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getMapView().getContext(), context.getResources().getString(R.string.SAVE_BTN), Toast.LENGTH_SHORT).show();
            }
        });

        // Broadcast receiver for processing SA from services
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String SelfID = null;

                //receving SprSerialService status
                Boolean status = intent.getBooleanExtra(PARAM_STATUS, true);
                if (status == false) {
                    getMapView().getContext().stopService(startSprServiceIntent);
                    sprSwitch.setChecked(false);
                    sprGpsSwitch.setChecked(false);
                }

                //Receiving HhmpUdpService input SA data
                String input_sa = intent.getStringExtra(UDP_STRING);
                SelfID = HHMP_GPS_ID.getText().toString();

                if ((hhmpSwitch.isChecked()) && SelfID != null) {
                    int row_count = tblLayoutHHMP.getChildCount();
                    if (row_count != 0) {
                        for (int i = 0; i < row_count; i++) {
                            TableRow hhmp_row = (TableRow) tblLayoutHHMP.getChildAt(i);

                            CheckBox chk_share = (CheckBox) hhmp_row.getChildAt(0);
                            EditText edt_combatid = (EditText) hhmp_row.getChildAt(1);
                            EditText edt_alias = (EditText) hhmp_row.getChildAt(2);
                            PluginSpinner spn_domain = (PluginSpinner) hhmp_row.getChildAt(3);
                            PluginSpinner spn_unit = (PluginSpinner) hhmp_row.getChildAt(4);

                            Boolean share = chk_share.isChecked();
                            String combatid = edt_combatid.getText().toString();
                            String alias = edt_alias.getText().toString();
                            int domain = spn_domain.getSelectedItemPosition();
                            int unit = spn_unit.getSelectedItemPosition();

                            MILSTDconverter std_conv = new MILSTDconverter();
                            if ((!SelfID.equals(combatid)) && (!combatid.equals(""))) {
                                LocateMarker.placeHhmpMarker(share, combatid, alias, input_sa, std_conv.milstd_conv_id(domain, unit));
                            }
                        }
                    }
                }

                if ((hhmpGpsSwitch.isChecked()) && SelfID != null) {
                    LocateMarker.placeHhmpSelf(mapView, input_sa);
                }

                //Receiving SprSerialService input SA data
                byte[] byteArray = intent.getByteArrayExtra(SPR_BYTE);
                SelfID = SPR_GPS_ID.getText().toString();
                if (sprSwitch.isChecked() && (byteArray != null)) {
                    SprSAparser sprparser = new SprSAparser();
                    Boolean AlertOn = sprparser.Alert(byteArray);
                    int row_count = tblLayoutSPR.getChildCount();
                    if (row_count != 0) {
                        for (int i = 0; i < row_count; i++) {
                            TableRow spr_row = (TableRow) tblLayoutSPR.getChildAt(i);
                            CheckBox chk_share = (CheckBox) spr_row.getChildAt(0);
                            EditText edt_combatid = (EditText) spr_row.getChildAt(1);
                            EditText edt_alias = (EditText) spr_row.getChildAt(2);
                            PluginSpinner spn_domain = (PluginSpinner) spr_row.getChildAt(3);
                            PluginSpinner spn_unit = (PluginSpinner) spr_row.getChildAt(4);

                            Boolean share = chk_share.isChecked();
                            String combatid = edt_combatid.getText().toString();
                            String alias = edt_alias.getText().toString();
                            int domain = spn_domain.getSelectedItemPosition();
                            int unit = spn_unit.getSelectedItemPosition();

                            MILSTDconverter std_conv = new MILSTDconverter();
                            if ((!SelfID.equals(combatid)) && (!combatid.equals(""))) {
                                // TODO Condition to check is alert on
                                if (AlertOn) {
                                    LocateMarker.placeSprAlert(combatid, alias, byteArray);
                                    if (sprparser.saPass(byteArray)) {
                                        getMapView().getMapController().panTo(new GeoPoint(sprparser.Latitude(), sprparser.Longtitude()), true);
                                        getMapView().getMapController().zoomTo(.00020d, true);
                                    }
                                } else {
                                    LocateMarker.placeSprMarker(share, combatid, alias, byteArray, std_conv.milstd_conv_id(domain, unit));
                                }
                            }
                        }
                    }
                }

                if ((sprGpsSwitch.isChecked()) && SelfID != null) {
                    LocateMarker.placeSprSelf(mapView, byteArray);
                }

            }

        };
        // new filter for BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // registration BroadcastReceiver
        context.registerReceiver(br, intFilt);

    }

    private void init(Context context) {
        String FieldDelimiter = ";";

        BufferedReader br;
        BufferedReader harris_br;
        BufferedReader spr_br;

        try {
            // Init settings
            br = new BufferedReader(new FileReader(harris_spr_settings));
            String line;
            line = br.readLine();
            EditText HHMP_GPS_ID = templateView.findViewById(R.id.edttxtSelfCombatIDHHMP);
            EditText SPR_GPS_ID = templateView.findViewById(R.id.edttxtSelfCombatIDSPR);

            String[] fields = line.split(FieldDelimiter, -1);
            HHMP_GPS_ID.setText(fields[0]);
            SPR_GPS_ID.setText(fields[1]);

            harris_br = new BufferedReader(new FileReader(harris_db));
            spr_br = new BufferedReader(new FileReader(spr_db));

            String harris_line;
            String spr_line;


            while ((harris_line = harris_br.readLine()) != null) {
                final String[] harris_fields = harris_line.split(FieldDelimiter, -1);

                final TableLayout t1 = templateView.findViewById(R.id.tblLayout_hhmp);
                final String tblTag;
                final int count;
                hhmp_count++;
                count = hhmp_count;
                tblTag = "HHMP";

                final TableRow tr_head = new TableRow(context);
                tr_head.setTag("Table" + tblTag + count);
                tr_head.setLayoutParams(new TableRow.LayoutParams());

                CheckBox chkShare = new CheckBox(context);
                chkShare.setTag("chkShare" + tblTag + count);
                chkShare.setPadding(0, 0, 0, 0);
                chkShare.setSelected(Boolean.parseBoolean(harris_fields[0]));
                chkShare.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 100));
                tr_head.addView(chkShare);

                ImageView chkImg = new ImageView(context);
                chkImg.setImageResource(share_img);
                chkImg.setLayoutParams(new TableRow.LayoutParams(50, 110));
                chkImg.setPadding(0, 0, 0, 0);
                chkImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                tr_head.addView(chkImg);

                EditText edComabatID = new EditText(context);
                edComabatID.setTag("CombatID" + tblTag + count);
                edComabatID.setHint(context.getResources().getString(R.string.COMBAT_ID));
                edComabatID.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.spinner_item));
                edComabatID.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                edComabatID.setText(harris_fields[1]);
                edComabatID.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 110, 1));
                tr_head.addView(edComabatID);

                EditText edAlias = new EditText(context);
                edAlias.setTag("Alias" + tblTag + count);
                edAlias.setHint(context.getResources().getString(R.string.MAP_ALIAS));
                edAlias.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.spinner_item));
                edAlias.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                edAlias.setText(harris_fields[2]);
                edAlias.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                tr_head.addView(edAlias);

                Spinner spnDomain = new PluginSpinner(context);
                spnDomain.setTag("spnDomain" + tblTag + count);
                String[] domain_str = context.getResources().getStringArray(R.array.domain_list);
                IconsAdapter domain_adapter = new IconsAdapter(context, iconsDOMAIN, domain_str);
                spnDomain.setAdapter(domain_adapter);
                spnDomain.setBackgroundResource(R.drawable.new_dark_button_bg);
                spnDomain.setPrompt(context.getResources().getString(R.string.CHOOSE_DOMAIN));
                spnDomain.setLayoutParams(new TableRow.LayoutParams(100, 100));
                spnDomain.setPadding(0, 0, 0, 0);
                spnDomain.setSelection(Integer.valueOf(harris_fields[3]));
                tr_head.addView(spnDomain);

                final Spinner spnUnit = new PluginSpinner(context);
                spnUnit.setBackgroundResource(R.drawable.new_dark_button_bg);
                spnUnit.setTag("spnUnit" + tblTag + count);
                IconsAdapter adapter_unit = null;
                String[] units = null;
                switch (Integer.valueOf(harris_fields[3])) {
                    case 0:
                        units = context.getResources().getStringArray(R.array.air_mil_list);
                        adapter_unit = new IconsAdapter(context, iconsAIRMIL, units);
                        break;
                    case 1:
                        units = context.getResources().getStringArray(R.array.gnd_vech_list);
                        adapter_unit = new IconsAdapter(context, iconsVEHICLE, units);
                        break;
                    case 2:
                        units = context.getResources().getStringArray(R.array.gnd_weap_list);
                        adapter_unit = new IconsAdapter(context, iconsWEAPON, units);
                        break;
                    case 3:
                        units = context.getResources().getStringArray(R.array.gnd_unt_cbt_list);
                        adapter_unit = new IconsAdapter(context, iconsCOMBAT, units);
                        break;
                    case 4:
                        units = context.getResources().getStringArray(R.array.gnd_unt_cs_list);
                        adapter_unit = new IconsAdapter(context, iconsCS, units);
                        break;
                    case 5:
                        units = context.getResources().getStringArray(R.array.gnd_unt_ss_list);
                        adapter_unit = new IconsAdapter(context, iconsCSS, units);
                        break;
                    case 6:
                        units = context.getResources().getStringArray(R.array.sfc_list);
                        adapter_unit = new IconsAdapter(context, iconsSEA, units);
                        break;
                    case 7:
                        units = context.getResources().getStringArray(R.array.sub_sfc_list);
                        adapter_unit = new IconsAdapter(context, iconsSUBSURFACE, units);
                        break;
                    case 8:
                        units = context.getResources().getStringArray(R.array.sof_list);
                        adapter_unit = new IconsAdapter(context, iconsSOF, units);
                        break;
                }
                spnUnit.setAdapter(adapter_unit);
                spnUnit.setPrompt(context.getResources().getString(R.string.CHOOSE_MARKER));
                spnUnit.setLayoutParams(new TableRow.LayoutParams(100, 100));
                spnUnit.setPadding(0, 0, 0, 0);
                tr_head.addView(spnUnit);

                spnDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        IconsAdapter unit = null;
                        String[] units = null;
                        switch (position) {
                            case 0:
                                units = context.getResources().getStringArray(R.array.air_mil_list);
                                unit = new IconsAdapter(context, iconsAIRMIL, units);
                                break;
                            case 1:
                                units = context.getResources().getStringArray(R.array.gnd_vech_list);
                                unit = new IconsAdapter(context, iconsVEHICLE, units);
                                break;
                            case 2:
                                units = context.getResources().getStringArray(R.array.gnd_weap_list);
                                unit = new IconsAdapter(context, iconsWEAPON, units);
                                break;
                            case 3:
                                units = context.getResources().getStringArray(R.array.gnd_unt_cbt_list);
                                unit = new IconsAdapter(context, iconsCOMBAT, units);
                                break;
                            case 4:
                                units = context.getResources().getStringArray(R.array.gnd_unt_cs_list);
                                unit = new IconsAdapter(context, iconsCS, units);
                                break;
                            case 5:
                                units = context.getResources().getStringArray(R.array.gnd_unt_ss_list);
                                unit = new IconsAdapter(context, iconsCSS, units);
                                break;
                            case 6:
                                units = context.getResources().getStringArray(R.array.sfc_list);
                                unit = new IconsAdapter(context, iconsSEA, units);
                                break;
                            case 7:
                                units = context.getResources().getStringArray(R.array.sub_sfc_list);
                                unit = new IconsAdapter(context, iconsSUBSURFACE, units);
                                break;
                            case 8:
                                units = context.getResources().getStringArray(R.array.sof_list);
                                unit = new IconsAdapter(context, iconsSOF, units);
                                break;
                        }
                        spnUnit.setAdapter(unit);
                        if (position == Integer.valueOf(harris_fields[3])) {
                            spnUnit.setSelection(Integer.valueOf(harris_fields[4]));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        return;
                    }
                });


                ImageButton btnRemove = new ImageButton(context);
                btnRemove.setTag("btnRemove" + tblTag + count);
                btnRemove.setImageResource(android.R.drawable.ic_menu_delete);
                btnRemove.setBackgroundResource(R.drawable.new_dark_button_bg);
                btnRemove.setPadding(0, 0, 0, 0);
                btnRemove.setLayoutParams(new TableRow.LayoutParams(100, 100));
                tr_head.addView(btnRemove);


                btnRemove.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        t1.removeView(tr_head);
                    }
                });


                t1.addView(tr_head, new TableLayout.LayoutParams());
            }

            while ((spr_line = spr_br.readLine()) != null) {
                final String[] spr_fields = spr_line.split(FieldDelimiter, -1);

                final TableLayout t2 = templateView.findViewById(R.id.tblLayout_spr);
                final String tblTag;
                final int count;
                spr_count++;
                count = spr_count;
                tblTag = "SPR";

                final TableRow tr_head = new TableRow(context);
                tr_head.setTag("Table" + tblTag + count);
                tr_head.setLayoutParams(new TableRow.LayoutParams());

                CheckBox chkShare = new CheckBox(context);
                chkShare.setTag("chkShare" + tblTag + count);
                chkShare.setPadding(0, 0, 0, 0);
                chkShare.setSelected(Boolean.parseBoolean(spr_fields[0]));
                chkShare.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 100));
                tr_head.addView(chkShare);

                ImageView chkImg = new ImageView(context);
                chkImg.setImageResource(share_img);
                chkImg.setLayoutParams(new TableRow.LayoutParams(50, 110));
                chkImg.setPadding(0, 0, 0, 0);
                chkImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                tr_head.addView(chkImg);

                EditText edComabatID = new EditText(context);
                edComabatID.setTag("CombatID" + tblTag + count);
                edComabatID.setHint(context.getResources().getString(R.string.COMBAT_ID));
                edComabatID.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.spinner_item));
                edComabatID.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                edComabatID.setText(spr_fields[1]);
                edComabatID.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 110, 1));
                tr_head.addView(edComabatID);

                EditText edAlias = new EditText(context);
                edAlias.setTag("Alias" + tblTag + count);
                edAlias.setHint(context.getResources().getString(R.string.MAP_ALIAS));
                edAlias.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.spinner_item));
                edAlias.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                edAlias.setText(spr_fields[2]);
                edAlias.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                tr_head.addView(edAlias);

                Spinner spnDomain = new PluginSpinner(context);
                spnDomain.setTag("spnDomain" + tblTag + count);
                String[] domain_str = context.getResources().getStringArray(R.array.domain_list);
                IconsAdapter domain_adapter = new IconsAdapter(context, iconsDOMAIN, domain_str);
                spnDomain.setAdapter(domain_adapter);
                spnDomain.setBackgroundResource(R.drawable.new_dark_button_bg);
                spnDomain.setPrompt(context.getResources().getString(R.string.CHOOSE_DOMAIN));
                spnDomain.setLayoutParams(new TableRow.LayoutParams(100, 100));
                spnDomain.setPadding(0, 0, 0, 0);
                spnDomain.setSelection(Integer.valueOf(spr_fields[3]));
                tr_head.addView(spnDomain);

                final Spinner spnUnit = new PluginSpinner(context);
                spnUnit.setBackgroundResource(R.drawable.new_dark_button_bg);
                spnUnit.setTag("spnUnit" + tblTag + count);
                IconsAdapter adapter_unit = null;
                String[] units = null;
                switch (Integer.valueOf(spr_fields[3])) {
                    case 0:
                        units = context.getResources().getStringArray(R.array.air_mil_list);
                        adapter_unit = new IconsAdapter(context, iconsAIRMIL, units);
                        break;
                    case 1:
                        units = context.getResources().getStringArray(R.array.gnd_vech_list);
                        adapter_unit = new IconsAdapter(context, iconsVEHICLE, units);
                        break;
                    case 2:
                        units = context.getResources().getStringArray(R.array.gnd_weap_list);
                        adapter_unit = new IconsAdapter(context, iconsWEAPON, units);
                        break;
                    case 3:
                        units = context.getResources().getStringArray(R.array.gnd_unt_cbt_list);
                        adapter_unit = new IconsAdapter(context, iconsCOMBAT, units);
                        break;
                    case 4:
                        units = context.getResources().getStringArray(R.array.gnd_unt_cs_list);
                        adapter_unit = new IconsAdapter(context, iconsCS, units);
                        break;
                    case 5:
                        units = context.getResources().getStringArray(R.array.gnd_unt_ss_list);
                        adapter_unit = new IconsAdapter(context, iconsCSS, units);
                        break;
                    case 6:
                        units = context.getResources().getStringArray(R.array.sfc_list);
                        adapter_unit = new IconsAdapter(context, iconsSEA, units);
                        break;
                    case 7:
                        units = context.getResources().getStringArray(R.array.sub_sfc_list);
                        adapter_unit = new IconsAdapter(context, iconsSUBSURFACE, units);
                        break;
                    case 8:
                        units = context.getResources().getStringArray(R.array.sof_list);
                        adapter_unit = new IconsAdapter(context, iconsSOF, units);
                        break;
                }
                spnUnit.setAdapter(adapter_unit);
                spnUnit.setPrompt(context.getResources().getString(R.string.CHOOSE_MARKER));
                spnUnit.setLayoutParams(new TableRow.LayoutParams(100, 100));
                spnUnit.setPadding(0, 0, 0, 0);
                tr_head.addView(spnUnit);

                spnDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        IconsAdapter unit = null;
                        String[] units = null;
                        switch (position) {
                            case 0:
                                units = context.getResources().getStringArray(R.array.air_mil_list);
                                unit = new IconsAdapter(context, iconsAIRMIL, units);
                                break;
                            case 1:
                                units = context.getResources().getStringArray(R.array.gnd_vech_list);
                                unit = new IconsAdapter(context, iconsVEHICLE, units);
                                break;
                            case 2:
                                units = context.getResources().getStringArray(R.array.gnd_weap_list);
                                unit = new IconsAdapter(context, iconsWEAPON, units);
                                break;
                            case 3:
                                units = context.getResources().getStringArray(R.array.gnd_unt_cbt_list);
                                unit = new IconsAdapter(context, iconsCOMBAT, units);
                                break;
                            case 4:
                                units = context.getResources().getStringArray(R.array.gnd_unt_cs_list);
                                unit = new IconsAdapter(context, iconsCS, units);
                                break;
                            case 5:
                                units = context.getResources().getStringArray(R.array.gnd_unt_ss_list);
                                unit = new IconsAdapter(context, iconsCSS, units);
                                break;
                            case 6:
                                units = context.getResources().getStringArray(R.array.sfc_list);
                                unit = new IconsAdapter(context, iconsSEA, units);
                                break;
                            case 7:
                                units = context.getResources().getStringArray(R.array.sub_sfc_list);
                                unit = new IconsAdapter(context, iconsSUBSURFACE, units);
                                break;
                            case 8:
                                units = context.getResources().getStringArray(R.array.sof_list);
                                unit = new IconsAdapter(context, iconsSOF, units);
                                break;
                        }
                        spnUnit.setAdapter(unit);
                        if (position == Integer.valueOf(spr_fields[3])) {
                            spnUnit.setSelection(Integer.valueOf(spr_fields[4]));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        return;
                    }
                });


                ImageButton btnRemove = new ImageButton(context);
                btnRemove.setTag("btnRemove" + tblTag + count);
                btnRemove.setImageResource(android.R.drawable.ic_menu_delete);
                btnRemove.setBackgroundResource(R.drawable.new_dark_button_bg);
                btnRemove.setPadding(0, 0, 0, 0);
                btnRemove.setLayoutParams(new TableRow.LayoutParams(100, 100));
                tr_head.addView(btnRemove);


                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t2.removeView(tr_head);
                    }
                });


                t2.addView(tr_head, new TableLayout.LayoutParams());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**************************** PUBLIC METHODS *****************************/

    public void disposeImpl() {
    }

    /**************************** INHERITED METHODS *****************************/

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action == null)
            return;

        if (action.equals(SHOW_PLUGIN)) {

            Log.d(TAG, "showing plugin drop down");
            showDropDown(templateView, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH,
                    HALF_HEIGHT, false);
        }
    }

    @Override
    public void onDropDownSelectionRemoved() {
    }

    @Override
    public void onDropDownVisible(boolean v) {
    }

    @Override
    public void onDropDownSizeChanged(double width, double height) {
    }

    @Override
    public void onDropDownClose() {
    }

}
