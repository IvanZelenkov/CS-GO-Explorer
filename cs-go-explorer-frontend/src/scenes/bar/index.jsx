import { Box } from "@mui/material";
import Header from "../../components/Header";
import BarChart from "../../components/BarChart";
import { useOutletContext } from "react-router-dom";

const Bar = () => {
	const [barChartData, setBarChartData] = useOutletContext();
	return (
		<Box m="20px">
			<Header title="Index Chart" subtitle="Simple Index Chart"/>
			<Box height="75vh">
				<BarChart barChartData={barChartData}/>
			</Box>
		</Box>
	);
};

export default Bar;