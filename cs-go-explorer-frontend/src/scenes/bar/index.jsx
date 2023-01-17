import { Box } from "@mui/material";
import Header from "../../components/Header";
import BarChart from "../../components/BarChart";
import { useOutletContext } from "react-router-dom";
import { motion } from "framer-motion";

const Bar = () => {
	const { barChartData, barKeys, barColors, barKeyName } = useOutletContext();

	return (
		<motion.div exit={{ opacity: 0 }}>
			<Box m="20px">
				<Header title="Index Chart"/>
				<Box height="80vh">
					<BarChart
						barChartData={barChartData}
						barKeys={barKeys}
						barColors={barColors}
						barKeyName={barKeyName}
					/>
				</Box>
			</Box>
		</motion.div>
	);
};

export default Bar;