import { ResponsivePie } from "@nivo/pie";
import { tokens } from "../theme";
import {Box, Typography, useTheme} from "@mui/material";

const PieChart = ({ chartState, chartSubtitle }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);
	const getColor = pie => pie.data.backgroundColor;

	return (
		<ResponsivePie
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
				}
			}}
			id={chartState.chartKeyName}
			margin={{ top: 40, right: 80, bottom: 80, left: 80 }}
			innerRadius={0.5}
			padAngle={0.7}
			tooltip={(item) => {
				if (chartSubtitle === "Weapon shots comparison") {
					if (item.datum.id === "Knife" || item.datum.id === "M4A1-S" || item.datum.id === "XM1014") {
						return (
							<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
								<Typography sx={{ color: "black", fontWeight: "bold", fontSize: "16px" }}>
									{item.datum.id}
								</Typography>
								<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.datum.value} shots</Typography>
							</Box>
						);
					}
					return (
						<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
							<Typography sx={{ color: item.datum.data.backgroundColor, fontWeight: "bold", fontSize: "16px" }}>
								{item.datum.id}
							</Typography>
							<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.datum.value} shots</Typography>
						</Box>
					);
				} else if (chartSubtitle === "Total rounds played") {
					if (item.datum.id === "St. Marc") {
						return (
							<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
								<Typography sx={{ color: "black", fontWeight: "bold", fontSize: "16px" }}>
									{item.datum.data.mapName}
								</Typography>
								<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.datum.data.value} rounds</Typography>
							</Box>
						);
					} else {
						return (
							<Box style={{ background: "white", padding: "9px 12px", border: "1px solid #CCC" }}>
								<Typography sx={{ color: item.datum.data.backgroundColor, fontWeight: "bold", fontSize: "16px" }}>
									{item.datum.data.mapName}
								</Typography>
								<Typography sx={{ color: "black", fontWeight: "bold" }}>{item.datum.data.value} rounds</Typography>
							</Box>
						);
					}
				}
			}}
			colors={getColor}
			cornerRadius={3}
			activeOuterRadiusOffset={8}
			borderColor={{
				from: "color",
				modifiers: [["darker", 0.2]]
			}}
			arcLinkLabelsSkipAngle={2}
			arcLinkLabelsDiagonalLength={25}
			arcLinkLabelsStraightLength={25}
			arcLinkLabelsTextColor={colors.steamColors[4]}
			arcLinkLabelsThickness={2}
			arcLinkLabelsColor={{ from: "color" }}
			enableArcLabels={true}
			arcLabelsRadiusOffset={0.5}
			arcLabelsSkipAngle={4}
			arcLabelsTextColor={{
				from: "",
				modifiers: [["darker", 2]]
			}}
			// Bottom color bar
			// legends={[
			// 	{
			// 		anchor: "bottom",
			// 		direction: "row",
			// 		justify: false,
			// 		translateX: 0,
			// 		translateY: 56,
			// 		itemsSpacing: 0,
			// 		itemWidth: 100,
			// 		itemHeight: 18,
			// 		itemTextColor: "#999",
			// 		itemDirection: "left-to-right",
			// 		itemOpacity: 1,
			// 		symbolSize: 18,
			// 		symbolShape: "circle",
			// 		effects: [
			// 			{
			// 				on: "hover",
			// 				style: {
			// 					itemTextColor: "#000"
			// 				}
			// 			}
			// 		]
			// 	}
			// ]}
		/>
	);
};

export default PieChart;