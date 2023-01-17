import { useTheme } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { tokens } from "../theme";

const BarChart = ({ barChartData, barKeys, barColors, barKeyName, isDashboard = false }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const getColor = bar => barColors[bar.id];

	return (
		<ResponsiveBar
			data={barChartData}
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
			keys={barKeys}
			indexBy={barKeyName}
			// tooltip={barChartData}
			// tooltipLabel={barChartData.weaponName}
			tooltip={(item) => {
				console.log(item)
				return (
					<div
						style={{
							background: "white",
							padding: "9px 12px",
							border: "1px solid #ccc",
							color: "black",
							fontWeight: "bold"
						}}
					>
						<div>{item.indexValue}</div>
						<div>{item.value} kills</div>
					</div>
				)
			}}
			margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
			padding={0.3}
			valueScale={{ type: "linear" }}
			indexScale={{ type: "band", round: true }}
			colors={getColor}
			defs={[
				{
					id: "dots",
					type: "patternDots",
					background: "inherit",
					color: "#38bcb2",
					size: 4,
					padding: 1,
					stagger: true,
				},
				{
					id: "lines",
					type: "patternLines",
					background: "inherit",
					color: "#eed312",
					rotation: -45,
					lineWidth: 6,
					spacing: 10,
				},
			]}
			borderColor={{
				from: "color",
				modifiers: [["darker", "1.6"]],
			}}
			axisTop={null}
			axisRight={null}
			axisBottom={{
				tickSize: 5,
				tickPadding: 5,
				tickRotation: 0,
				legend: isDashboard ? undefined : "Weapon",
				legendPosition: "middle",
				legendOffset: 32,
			}}
			axisLeft={{
				tickSize: 5,
				tickPadding: 5,
				tickRotation: 0,
				legend: isDashboard ? undefined : "Kills",
				legendPosition: "middle",
				legendOffset: -40,
			}}
			enableLabel={true}
			labelSkipWidth={12}
			labelSkipHeight={12}
			labelTextColor={{
				from: "",
				modifiers: [["darker", 1.6]]
			}}
			// Commented out the right hand side color bar for now
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