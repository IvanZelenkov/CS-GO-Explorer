import { ResponsivePie } from "@nivo/pie";
import { tokens } from "../theme";
import { useTheme } from "@mui/material";

const PieChart = ({ chartData, chartKeys, chartColors, chartKeyName, isDashboard = false }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);

	return (
		<ResponsivePie
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
				}
			}}
			margin={{ top: 40, right: 80, bottom: 80, left: 80 }}
			innerRadius={0.5}
			padAngle={0.7}
			cornerRadius={3}
			activeOuterRadiusOffset={8}
			borderColor={{
				from: "color",
				modifiers: [["darker", 0.2]]
			}}
			arcLinkLabelsSkipAngle={3}
			arcLinkLabelsTextColor={colors.grey[100]}
			arcLinkLabelsThickness={2}
			arcLinkLabelsColor={{ from: "color" }}
			enableArcLabels={true}
			arcLabelsRadiusOffset={0.5}
			arcLabelsSkipAngle={7}
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