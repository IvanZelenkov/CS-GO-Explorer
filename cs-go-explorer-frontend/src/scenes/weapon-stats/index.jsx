import { useReducer } from "react";
import { Box, Button, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { Link, Outlet, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { tokens } from "../../theme";
import Refresh from "@mui/icons-material/Refresh";
import BarChartOutlinedIcon from "@mui/icons-material/BarChartOutlined";
import PieChartOutlineOutlinedIcon from "@mui/icons-material/PieChartOutlineOutlined";
import axios from "axios";
import Header from "../../components/Header";
import SidebarBackgroundImage from "../../assets/images/backgrounds/sidebar_and_tables_background.png";
import loading from "react-useanimations/lib/loading";
import UseAnimations from "react-useanimations";

function chartReducer(chartState, action) {
	switch (action.type) {
		case 'setChartData':
			return { ...chartState, chartData: action.payload };
		case 'setChartKeys':
			return { ...chartState, chartKeys: chartState.chartKeys = action.payload };
		case 'setChartKeyName':
			return { ...chartState, chartKeyName: chartState.chartKeyName = action.payload };
		case 'setChartColors':
			return { ...chartState, chartColors: chartState.chartColors = action.payload };
		default:
			return { ...chartState };
	}
}

const WeaponStats = () => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const location = useLocation();
	const [infoLoaded, setInfoLoaded] = useState(false);
	const [userStats, setUserStats] = useState({});
	const [chartState, dispatch] = useReducer(chartReducer, {
		chartData: {},
		chartKeys: [],
		chartKeyName: "",
		chartColors: {}
	});
	const weapons = ["AK-47", "AUG", "AWP", "PP-Bizon", "Deagle", "Berettas", "FAMAS", "Five-seveN",
		"G3SG1", "Galil AR", "Glock-18", "Grenade", "HK P2000", "Knife", "M4A1-S", "M249",
		"MAC-10", "MAG-7", "Molotov", "MP7", "MP9", "Negev", "Nova", "P90", "P250",
		"Sawed-Off", "SCAR-20", "SG 556", "SSG 08", "Zeus x27", "Tec-9", "UMP-45", "XM1014"];
	const weaponKeys = ["ak47", "aug", "awp", "bizon", "deagle", "elite", "famas", "fiveseven",
		"g3sg1", "galilar", "glock", "hegrenade", "hkp2000", "knife", "m4a1", "m249",
		"mac10", "mag7", "molotov", "mp7", "mp9", "negev", "nova", "p90", "p250",
		"sawedoff", "scar20", "sg556", "ssg08", "taser", "tec9", "ump45", "xm1014"];
	const customColors = { "ak47": "#C0392B", "aug": "#E74C3C", "awp": "#9B59B6", "bizon": "#8E44AD",
		"deagle": "#2980B9", "elite": "#3498DB", "famas": "#1ABC9C", "fiveseven": "#16A085",
		"g3sg1": "#27AE60", "galilar": "#2ECC71", "glock": "#F1C40F", "hegrenade": "#F39C12",
		"hkp2000": "#D35400", "knife": "#ECF0F1", "m4a1": "#CACFD2", "m249": "#95A5A6",
		"mac10": "#7F8C8D", "mag7": "#A93226", "molotov": "#2C3E50", "mp7": "#CB4335",
		"mp9": "#884EA0", "negev": "#7D3C98", "nova": "#2471A3", "p90": "#2E86C1",
		"p250": "#17A589", "sawedoff": "#138D75", "scar20": "#229954", "sg556": "#28B463",
		"ssg08": "#D4AC0D", "taser": "#D68910", "tec9": "#CA6F1E", "ump45": "#BA4A00",
		"xm1014": "#D0D3D4" };

	const getUserStats = async () => {
		try {
			const response = await axios.get(
				"https://" +
				process.env.REACT_APP_REST_API_ID +
				".execute-api.us-east-1.amazonaws.com/ProductionStage/GetUserStatsForGame?steamid="
				+ JSON.parse(localStorage.getItem("steam_id"))
			);
			setUserStats(reformatUserStatsJson(JSON.parse(response.data.body)));
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		getUserStats();
	}, []);


	const reformatUserStatsJson = (overallStats) => {
		if (overallStats.hasOwnProperty('playerstats')) {
			dispatch({type: 'setChartKeys', payload: weaponKeys});

			let weaponStats = [];
			for (let i = 0; i < weaponKeys.length; i++)
				weaponStats.push({
					weaponName: weaponKeys[i],
					totalKills: "",
					totalShots: "",
					totalHits: ""
				});

			let newJsonUserStats = {
				steamID: overallStats.playerstats.steamID,
				gameName: "CS:GO",
				stats: weaponStats
			};

			for (let i = 0; i < overallStats.playerstats.stats.length; i++) {
				let dataItem = overallStats.playerstats.stats[i];
				if (!dataItem.name.includes("total_kills") &&
					!dataItem.name.includes("total_shots") &&
					!dataItem.name.includes("total_hits")) {
					continue;
				}
				let weapon = weaponKeys.find((weaponName) => dataItem.name.includes(weaponName));
				if (weapon !== undefined) {
					if (dataItem.name.includes("total_kills")) {
						newJsonUserStats.stats[weaponKeys.indexOf(weapon)].totalKills = dataItem.value;
						newJsonUserStats.stats[weaponKeys.indexOf(weapon)].value = dataItem.value;
					} else if (dataItem.name.includes("total_shots"))
						newJsonUserStats.stats[weaponKeys.indexOf(weapon)].totalShots = dataItem.value;
					else if (dataItem.name.includes("total_hits"))
						newJsonUserStats.stats[weaponKeys.indexOf(weapon)].totalHits = dataItem.value;
				}
			}
			reformatUserStatsForChart(newJsonUserStats);
			setInfoLoaded(true);
			return newJsonUserStats;
		} else {
			setInfoLoaded(false);
			return {};
		}
	}

	const reformatUserStatsForChart = (reformattedUserStats) => {
		let weaponStats = [];
		for (let i = 0; i < weapons.length; i++) {
			let totalShots = reformattedUserStats.stats[i].totalShots;

			if (totalShots.length !== 0) {
				weaponStats.push({
					id: weapons[i],
					value: totalShots,
					weaponName: weapons[i],
					[reformattedUserStats.stats[i].weaponName]: reformattedUserStats.stats[i].totalKills,
					backgroundColor: customColors[weaponKeys[i]]
				});
			}
		}

		dispatch({ type: 'setChartKeyName', payload: "weaponName" });
		dispatch({ type: 'setChartData', payload: weaponStats });
		dispatch({ type: 'setChartColors', payload: customColors });
	}

	const columns = [
		{
			field: "weaponName",
			headerName: "Weapon",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				if (!value)
					return "";
				return (
					<Box display="flex" justifyContent="center" alignItems="center">
						<Box
							component="img"
							alt={value}
							width="9vw"
							height="12vh"
							src={require("../../assets/images/weapons/" + value + ".webp")}
							style={{ justifyContent: "center", alignItems: "center", fontSize: "1.2vh" }}
						/>
					</Box>
				);
			}
		},
		{
			field: "totalKills",
			headerName: "Total Kills",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				if (!value)
					return "";
				else
					return (
						<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
							{value}
						</Box>
					);
			}
		},
		{
			field: "totalHits",
			headerName: "Total Hits",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				if (!value)
					return "";
				else
					return (
						<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
							{value}
						</Box>
					);
			}
		},
		{
			field: "totalShots",
			headerName: "Total Shots",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				if (!value)
					return "";
				else
					return (
						<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
							{value}
						</Box>
					);
			}
		},
		{
			field: "totalHits/totalShots",
			headerName: "Hits/Shots %",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.totalHits || !row.totalShots) {
					return "";
				}
				else {
					return (
						<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
							{(row.totalHits / row.totalShots * 100).toFixed(2)} %
						</Box>
					);
				}
			}
		},
		{
			field: "shotsAvgToKill",
			headerName: "Amount of bullets to kill",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.totalHits || !row.totalShots) {
					return "";
				}
				else {
					return (
						<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
							{(row.totalShots / row.totalKills).toFixed(2)}
						</Box>
					);
				}
			}
		}
	];

	if (infoLoaded === false || userStats === {}) {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="Weapon Stats" subtitle="Explore weapon stats"/>
					<Box sx={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)' }}>
						<UseAnimations animation={loading} size={50} fillColor={"#7da10e"} strokeColor={"#7da10e"}/>
					</Box>
				</Box>
			</motion.div>
		);
	} else if (location.pathname === "/weapon-stats") {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="Weapon Stats" subtitle="Explore weapon stats"/>
					<Box display="flex" justifyContent="space-between" alignItems="center">
						<Button
							sx={{
								backgroundColor: "custom.steamColorA",
								color: "custom.steamColorD",
								fontSize: "1vh",
								fontWeight: "bold",
								padding: "0.8vh 1.2vh",
								":hover": {
									backgroundColor: "custom.steamColorF"
								}
							}}
							onClick={() => {
								setInfoLoaded(false);
								getUserStats();
							}}
						>
							<Refresh sx={{ marginRight: "0.5vh" }}/>
							Refresh
						</Button>
						<Box display="flex" flexDirection="row">
							<Button
								sx={{
									backgroundColor: "custom.steamColorA",
									color: "custom.steamColorD",
									fontSize: "1vh",
									fontWeight: "bold",
									padding: "0.8vh 1.2vh",
									marginRight: "2vh",
									":hover": {
										backgroundColor: "custom.steamColorF"
									}
								}}
								component={Link}
								to={location.pathname + "/bar"}
							>
								<BarChartOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
								Kill amount stats
							</Button>
							<Button
								sx={{
									backgroundColor: "custom.steamColorA",
									color: "custom.steamColorD",
									fontSize: "1vh",
									fontWeight: "bold",
									padding: "0.8vh 1.2vh",
									":hover": {
										backgroundColor: "custom.steamColorF"
									}
								}}
								component={Link}
								to={location.pathname + "/pie"}
							>
								<PieChartOutlineOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
								Weapon shots comparison
							</Button>
						</Box>
					</Box>
					<Box
						margin="2vh 0 0 0"
						height="70vh"
						sx={{
							"& .MuiDataGrid-root": {
								border: "none"
							},
							"& .MuiDataGrid-cell": {
								borderBottom: "none"
							},
							"& .name-column--cell": {
								color: "custom.steamColorE",
								textTransform: "capitalize"
							},
							"& .MuiDataGrid-columnHeaders": {
								backgroundImage: `url(${SidebarBackgroundImage}) !important`,
								backgroundSize: 'cover',
								backgroundRepeat  : 'no-repeat',
								backgroundPosition: 'center',
								borderBottom: "none",
								fontSize: "1.2vh"
							},
							"& .MuiDataGrid-virtualScroller": {
								backgroundColor: colors.primary[400]
							},
							"& .MuiDataGrid-footerContainer": {
								backgroundImage: `url(${SidebarBackgroundImage}) !important`,
								backgroundSize: 'cover',
								backgroundRepeat  : 'no-repeat',
								backgroundPosition: 'center',
								borderTop: "none"
							},
							"& .MuiCheckbox-root": {
								color: `${colors.steamColors[6]} !important`
							}
						}}
					>
						{infoLoaded && <DataGrid
							rows={userStats.stats}
							getRowId={((row) => row?.weaponName)}
							columns={columns}
							rowHeight={150}
						/>}
					</Box>
					<Outlet/>
				</Box>
			</motion.div>
		);
	} else if (location.pathname === "/weapon-stats/bar") {
		return (
			<Outlet context={{ chartState, chartSubtitle: "Kill amount stats" }}/>
		);
	} else if (location.pathname === "/weapon-stats/pie") {
		return (
			<Outlet context={{ chartState, chartSubtitle: "Weapon shots comparison" }}/>
		);
	}
};

export default WeaponStats;