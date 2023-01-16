import { useTheme } from "@mui/material";
import { ResponsiveBar } from "@nivo/bar";
import { tokens } from "../theme";

const BarChart = ({ barChartData, isDashboard = false }) => {
	const theme = useTheme();
	const colors = tokens(theme.palette.mode);

	console.log(barChartData);

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
			}}
			keys={["ak47", "aug", "awp", "bizon", "deagle", "elite", "famas", "fiveseven",
				   "g3sg1", "galilar", "glock", "hegrenade", "hkp2000", "knife", "m4a1", "m249",
				   "mac10", "mag7", "molotov", "mp7", "mp9", "negev", "nova", "p90", "p250",
				   "sawedoff", "scar20", "sg556", "ssg08", "taser", "tec9", "ump45", "xm1014"]}
			indexBy="weaponName"
			margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
			padding={0.3}
			valueScale={{ type: "linear" }}
			indexScale={{ type: "band", round: true }}
			colors={{ scheme: "nivo" }}
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
			enableLabel={false}
			labelSkipWidth={12}
			labelSkipHeight={12}
			labelTextColor={{
				from: "color",
				modifiers: [["darker", 1.6]],
			}}
			legends={[
				{
					dataFrom: "keys",
					anchor: "bottom-right",
					direction: "column",
					justify: false,
					translateX: 120,
					translateY: 0,
					itemsSpacing: 2,
					itemWidth: 100,
					itemHeight: 20,
					itemDirection: "left-to-right",
					itemOpacity: 0.85,
					symbolSize: 20,
					effects: [
						{
							on: "hover",
							style: {
								itemOpacity: 1,
							},
						},
					],
				},
			]}
			role="application"
			barAriaLabel={function (e) {
				return e.id + ": " + e.formattedValue + " in country: " + e.indexValue;
			}}
		/>
	);
};

export default BarChart;