import { Box, Typography, useTheme } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { tokens } from "../theme";

const BarChart = ({ chartState, chartSubtitle }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const getColor = bar => bar.data.backgroundColor;

	return (
		<ResponsiveBar
			data={chartState.chartData}
			theme={{
				axis: {
					domain: {
						line: {
							stroke: colors.steamColors[4]
						}
					},
					legend: {
						text: {
							fill: colors.steamColors[6],
							fontSize: "14px",
							fontWeight: "bold"
						}
					},
					ticks: {
						line: {
							stroke: colors.steamColors[4],
							strokeWidth: 1
						},
						text: {
							fill: colors.steamColors[4]
						}
					}
				},
				legends: {
					text: {
						fill: colors.steamColors[4]
					}
				},
				fontSize: "0.75vh"
			}}
			keys={chartState.chartKeys}
			indexBy={chartState.chartKeyName}
			tooltip={(item) => {
				if (chartSubtitle === "Kill amount stats") {
					if (item.indexValue === "Knife" || item.indexValue === "M4A1-S" || item.indexValue === "XM1014") {
						return (
							<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
								<Typography sx={{ color: "black", fontWeight: "bold", fontSize: "16px" }}>
									{item.indexValue}
								</Typography>
								<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.value} kills</Typography>
							</Box>
						);
					}
					return (
						<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
							<Typography sx={{ color: item.color, fontWeight: "bold", fontSize: "16px" }}>{item.indexValue}</Typography>
							<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.value} kills</Typography>
						</Box>
					);
				} else if (chartSubtitle === "Map round win rates") {
					if (item.indexValue === "St. Marc") {
						return (
							<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
								<Typography sx={{ color: "black", fontWeight: "bold", fontSize: "16px" }}>
									{item.indexValue}
								</Typography>
								<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.value} %</Typography>
							</Box>
						);
					} else {
						return (
							<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
								<Typography sx={{ color: item.color, fontWeight: "bold", fontSize: "16px" }}>
									{item.indexValue}
								</Typography>
								<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.value} %</Typography>
							</Box>
						);
					}
				}
			}}
			margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
			padding={0.4}
			valueScale={{ type: "linear" }}
			indexScale={{ type: "band", round: true }}
			colors={getColor}
			axisTop={null}
			axisRight={null}
			axisBottom={{
				tickSize: 10,
				tickPadding: 5,
				tickRotation: 0,
				legend: chartSubtitle === "Kill amount stats" ? "Weapons" : "Maps",
				legendPosition: "middle",
				legendOffset: 42
			}}
			axisLeft={{
				tickSize: 10,
				tickPadding: 5,
				tickRotation: 0,
				legend: chartSubtitle === "Kill amount stats" ? "Kills" : "Map round win rates %",
				legendPosition: "middle",
				legendOffset: -50
			}}
			enableLabel={true}
			labelSkipWidth={12}
			labelSkipHeight={12}
			labelTextColor={{
				from: "",
				modifiers: [["darker", 1.2]]
			}}
			// Right-hand side color bar
			// legends={[
			// 	{
			// 		dataFrom: "keys",
			// 		anchor: "bottom-right",
			// 		direction: "column",
			// 		justify: false,
			// 		translateX: 120,
			// 		translateY: 0,
			// 		itemsSpacing: 2,
			// 		itemWidth: 100,
			// 		itemHeight: 20,
			// 		itemDirection: "left-to-right",
			// 		itemOpacity: 1,
			// 		symbolSize: 20,
			// 		effects: [
			// 			{
			// 				on: "hover",
			// 				style: {
			// 					itemOpacity: 0.8
			// 				}
			// 			}
			// 		]
			// 	}
			// ]}
			role="application"
		/>
	);
};

export default BarChart;