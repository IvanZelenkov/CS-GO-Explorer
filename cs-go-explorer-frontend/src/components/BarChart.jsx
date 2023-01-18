import { Box, Typography, useTheme } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { tokens } from "../theme";

const BarChart = ({ chartData, chartKeys, chartColors, chartKeyName, isDashboard = false }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const getColor = bar => chartColors[bar.id];

	return (
		<ResponsiveBar
			data={chartData}
			theme={{
				axis: {
					domain: {
						line: {
							stroke: colors.grey[100]
						}
					},
					legend: {
						text: {
							fill: colors.grey[100]
						}
					},
					ticks: {
						line: {
							stroke: colors.grey[100],
							strokeWidth: 1
						},
						text: {
							fill: colors.grey[100]
						}
					}
				},
				legends: {
					text: {
						fill: colors.grey[100]
					}
				},
				fontSize: "0.75vh"
			}}
			keys={chartKeys}
			indexBy={chartKeyName}
			tooltip={(item) => {
				return (
					<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
						<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.indexValue}</Typography>
						<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.value} kills</Typography>
					</Box>
				)
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
				legend: isDashboard ? undefined : "Weapons",
				legendPosition: "middle",
				legendOffset: 42
			}}
			axisLeft={{
				tickSize: 10,
				tickPadding: 5,
				tickRotation: 0,
				legend: isDashboard ? undefined : "Kills",
				legendPosition: "middle",
				legendOffset: -50
			}}
			enableLabel={true}
			labelSkipWidth={12}
			labelSkipHeight={12}
			labelTextColor={{
				from: "",
				modifiers: [["darker", 1.6]]
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