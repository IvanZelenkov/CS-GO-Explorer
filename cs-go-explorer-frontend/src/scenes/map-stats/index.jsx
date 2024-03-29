import { useReducer } from "react";
import { Box, Button, useTheme } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { Link, Outlet, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { tokens } from "../../theme";
import Refresh from "@mui/icons-material/Refresh";
import BarChartOutlinedIcon from "@mui/icons-material/BarChartOutlined";
import axios from "axios";
import Header from "../../components/Header";
import SidebarBackgroundImage from "../../assets/images/backgrounds/sidebar_and_tables_background.jpg";
import PieChartOutlineOutlinedIcon from "@mui/icons-material/PieChartOutlineOutlined";
import UseAnimations from 'react-useanimations';
import loading from 'react-useanimations/lib/loading';
import Loader from "../../components/Loader";

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

const MapStats = () => {
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
	const maps = ["Dust II", "Inferno", "Nuke", "Vertigo", "Office", "Train", "Lake", "Assault",
		"Cobblestone", "Italy", "Monastery", "Safehouse", "Shoots", "St. Marc", "Bank", "Sugarcane",
		"Baggage", "Aztec"];
	const mapKeys = ["dust2", "inferno", "nuke", "vertigo", "office", "train", "lake", "assault",
		"cbble", "italy", "monastery", "safehouse", "shoots", "stmarc", "bank", "sugarcane", "baggage", "aztec"];
	const customColors = { "dust2": "#C0392B", "inferno": "#E74C3C", "nuke": "#9B59B6", "vertigo": "#8E44AD",
		"office": "#2980B9", "train": "#3498DB", "lake": "#1ABC9C", "assault": "#16A085", "cbble": "#27AE60",
		"italy": "#2ECC71", "monastery": "#F1C40F", "safehouse": "#F39C12", "shoots": "#D35400",
		"stmarc": "#ECF0F1", "bank": "#00BFFF", "sugarcane": "#005CFF", "baggage": "#BCFF00", "aztec": "#46E4E8" };

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
			dispatch({ type: 'setChartKeys', payload: mapKeys });

			let mapStats = [];
			for (let i = 0; i < mapKeys.length; i++)
				mapStats.push({
					mapName: mapKeys[i],
					totalRoundWins: "",
					totalRounds: ""
				});

			let newJsonUserStats = {
				stats: mapStats
			};

			for (let i = 0; i < overallStats.playerstats.stats.length; i++) {
				let dataItem = overallStats.playerstats.stats[i];
				if (!dataItem.name.includes("total_wins_map")
					&& !dataItem.name.includes("total_rounds_map"))
					continue;

				let map = mapKeys.find((mapName) => dataItem.name.includes(mapName));
				if (map !== undefined)
					if (dataItem.name.includes("total_wins_map"))
						newJsonUserStats.stats[mapKeys.indexOf(map)].totalRoundWins = dataItem.value;
					else if (dataItem.name.includes("total_rounds_map"))
						newJsonUserStats.stats[mapKeys.indexOf(map)].totalRounds = dataItem.value;
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
		let mapStats = [];
		for (let i = 0; i < maps.length; i++) {
			let totalRounds = reformattedUserStats.stats[i].totalRounds;

			if (totalRounds.length !== 0 && reformattedUserStats.stats[i].totalRoundWins <= totalRounds ) {
				mapStats.push({
					id: mapKeys[i],
					value: totalRounds,
					mapName: maps[i],
					[reformattedUserStats.stats[i].mapName]: (reformattedUserStats.stats[i].totalRoundWins
						/ totalRounds * 100).toFixed(2),
					backgroundColor: customColors[mapKeys[i]]
				});
			}
		}

		dispatch({ type: 'setChartKeyName', payload: "mapName" });
		dispatch({ type: 'setChartData', payload: mapStats });
		dispatch({ type: 'setChartColors', payload: customColors });
	}

	const columns = [
		{
			field: "mapName",
			headerName: "Map",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ value }) => {
				if (!value) {
					return "";
				}
				return (
					<Box display="flex" justifyContent="center" alignItems="center">
						<Box
							component="img"
							alt={value}
							width="17vw"
							height="17vh"
							src={require("../../assets/images/maps/" + value + ".webp")}
							style={{ justifyContent: "center", alignItems: "center", fontSize: "1.2vh", borderRadius: "10px" }}
						/>
					</Box>
				);
			}
		},
		{
			field: "totalRoundWins",
			headerName: "Total Round Wins",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.totalRoundWins || row.totalRoundWins > row.totalRounds)
					return "";
				return (
					<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
						{row.totalRoundWins}
					</Box>
				);
			}
		},
		{
			field: "totalRounds",
			headerName: "Total Rounds",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.totalRounds || row.totalRoundWins > row.totalRounds)
					return "";
				return (
					<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
						{row.totalRounds}
					</Box>
				);
			}
		},
		{
			field: "totalRoundWins/totalRounds",
			headerName: "Round Win Rates %",
			flex: 1,
			headerAlign: "center",
			align: "center",
			renderCell: ({ row }) => {
				if (!row.totalRoundWins || !row.totalRounds || row.totalRoundWins > row.totalRounds) {
					return "";
				} else {
					return (
						<Box display="flex" justifyContent="center" alignItems="center" sx={{ fontSize: "1.2vh" }}>
							{(row.totalRoundWins / row.totalRounds * 100).toFixed(2)} %
						</Box>
					);
				}
			}
		}
	];

	if (infoLoaded === false || userStats === {}) {
		return (
			<Box margin="1.5vh">
				<Header title="Map Stats" subtitle="Explore map stats"/>
				<Loader colors={colors}/>
				<Box display="flex" justifyContent="space-between" alignItems="center">
					<Button
						sx={{
							backgroundColor: "custom.steamColorC",
							color: "custom.steamColorD",
							fontSize: "1vh",
							fontWeight: "bold",
							fontFamily: "Montserrat",
							padding: "0.8vh 1.2vh",
							border: `0.2vh solid #5ddcff`,
							boxShadow: "0px 0px 10px #5ddcff",
							":hover": {
								backgroundColor: "custom.steamColorB"
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
								backgroundColor: "custom.steamColorC",
								color: "custom.steamColorD",
								fontSize: "1vh",
								fontWeight: "bold",
								fontFamily: "Montserrat",
								padding: "0.8vh 1.2vh",
								marginRight: "2vh",
								border: `0.2vh solid #5ddcff`,
								boxShadow: "0px 0px 10px #5ddcff",
								":hover": {
									backgroundColor: "custom.steamColorB"
								}
							}}
							component={Link}
							to={location.pathname + "/bar"}
						>
							<BarChartOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
							Map round win rates
						</Button>
						<Button
							sx={{
								backgroundColor: "custom.steamColorC",
								color: "custom.steamColorD",
								fontSize: "1vh",
								fontWeight: "bold",
								fontFamily: "Montserrat",
								padding: "0.8vh 1.2vh",
								border: `0.2vh solid #5ddcff`,
								boxShadow: "0px 0px 10px #5ddcff",
								":hover": {
									backgroundColor: "custom.steamColorB"
								}
							}}
							component={Link}
							to={location.pathname + "/pie"}
						>
							<PieChartOutlineOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
							Total rounds played
						</Button>
					</Box>
				</Box>
			</Box>
		)
	} else if (location.pathname === "/map-stats") {
		return (
			<motion.div exit={{ opacity: 0 }}>
				<Box margin="1.5vh">
					<Header title="Map Stats" subtitle="Explore map stats"/>
					<Box display="flex" justifyContent="space-between" alignItems="center">
						<Button
							sx={{
								backgroundColor: "custom.steamColorC",
								color: "custom.steamColorD",
								fontSize: "1vh",
								fontWeight: "bold",
								fontFamily: "Montserrat",
								padding: "0.8vh 1.2vh",
								border: `0.2vh solid #5ddcff`,
								boxShadow: "0px 0px 10px #5ddcff",
								":hover": {
									backgroundColor: "custom.steamColorB"
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
									backgroundColor: "custom.steamColorC",
									color: "custom.steamColorD",
									fontSize: "1vh",
									fontWeight: "bold",
									fontFamily: "Montserrat",
									padding: "0.8vh 1.2vh",
									marginRight: "2vh",
									border: `0.2vh solid #5ddcff`,
									boxShadow: "0px 0px 10px #5ddcff",
									":hover": {
										backgroundColor: "custom.steamColorB"
									}
								}}
								component={Link}
								to={location.pathname + "/bar"}
							>
								<BarChartOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
								Map round win rates
							</Button>
							<Button
								sx={{
									backgroundColor: "custom.steamColorC",
									color: "custom.steamColorD",
									fontSize: "1vh",
									fontWeight: "bold",
									fontFamily: "Montserrat",
									padding: "0.8vh 1.2vh",
									border: `0.2vh solid #5ddcff`,
									boxShadow: "0px 0px 10px #5ddcff",
									":hover": {
										backgroundColor: "custom.steamColorB"
									}
								}}
								component={Link}
								to={location.pathname + "/pie"}
							>
								<PieChartOutlineOutlinedIcon sx={{ marginRight: "0.5vh" }}/>
								Total rounds played
							</Button>
						</Box>
					</Box>
					<Box
						margin="2vh 0 0 0"
						height="70vh"
						sx={{
							"& .MuiDataGrid-root": {
								border: "none",
							},
							"& .MuiDataGrid-cell": {
								borderBottom: "none",
							},
							"& .name-column--cell": {
								color: "custom.steamColorE",
								textTransform: "capitalize"
							},
							"& .MuiDataGrid-columnHeaders": {
								backgroundColor: colors.steamColors[3],
								borderBottom: "none",
								fontSize: "1.2vh",
								fontFamily: "Montserrat",
								color: colors.steamColors[6]
							},
							"& .MuiDataGrid-virtualScroller": {
								backgroundColor: colors.primary[400],
								fontFamily: "Montserrat",
								color: colors.steamColors[4]
							},
							"& .MuiDataGrid-footerContainer": {
								backgroundColor: colors.steamColors[3],
								borderTop: "none"
							},
							"& .MuiCheckbox-root": {
								color: `${colors.steamColors[6]} !important`
							},
						}}
					>
						{infoLoaded && <DataGrid
							rows={userStats.stats}
							getRowId={((row) => row?.mapName)}
							columns={columns}
							rowHeight={250}
						/>}
					</Box>
				</Box>
			</motion.div>
		);
	} else if (location.pathname === "/map-stats/bar") {
		return (
			<Outlet context={{ chartState, chartSubtitle: "Map round win rates" }}/>
		);
	} else if (location.pathname === "/map-stats/pie") {
		return (
			<Outlet context={{ chartState, chartSubtitle: "Total rounds played" }}/>
		);
	}
};

export default MapStats;