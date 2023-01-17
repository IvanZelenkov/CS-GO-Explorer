import { useState } from 'react';
import { ColorModeContext, useMode } from './theme';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { CSSTransition, TransitionGroup } from 'react-transition-group';
import { AnimatePresence, motion } from "framer-motion";
import { Routes, Route } from 'react-router-dom';
import Sidebar from './scenes/global/Sidebar';
import Dashboard from './scenes/dashboard';
import Topbar from "./scenes/global/Topbar";
import Friends from './scenes/friends';
import Contacts from './scenes/contacts';
import Invoices from './scenes/invoices';
import Form from './scenes/form';
import Calendar from './scenes/calendar';
import FAQ from './scenes/faq';
import MapsStats from "./scenes/maps-stats";
import Bar from './scenes/bar';
import Pie from './scenes/pie';
import Line from './scenes/line';
import Geography from './scenes/geography';
import WeaponsStats from "./scenes/weapons-stats";
import { useLocation } from "react-router-dom";

function App() {
	const [theme, colorMode] = useMode();
	const [isSidebar, setIsSidebar] = useState(true);
	const location = useLocation();

	return (
		<ColorModeContext.Provider value={colorMode}>
			<ThemeProvider theme={theme}>
				<CssBaseline/>
					<div className="app">
						<Sidebar isSidebar={isSidebar}/>
						<main className="content">
							<Topbar setIsSidebar={setIsSidebar}/>
							<AnimatePresence mode='wait'>
								<Routes location={location} key={location.pathname}>
									<Route exact path="/" element={<Dashboard/>}/>
									<Route exact path="/friends" element={<Friends/>}/>
									<Route exact path="/contacts" element={<Contacts/>}/>
									<Route exact path="/invoices" element={<Invoices/>}/>
									<Route exact path="/form" element={<Form/>}/>
									<Route exact path="/calendar" element={<Calendar/>}/>
									<Route exact path="/faq" element={<FAQ/>}/>
									<Route exact path="/weapons-stats" element={<WeaponsStats/>}>
										<Route exact path="bar" element={<Bar/>}/>
									</Route>
									<Route exact path="/maps-stats" element={<MapsStats/>}/>
									<Route exact path="/pie" element={<Pie/>}/>
									<Route exact path="/line" element={<Line/>}/>
									<Route exact path="/geography" element={<Geography/>}/>
								</Routes>
							</AnimatePresence>
						</main>
					</div>
			</ThemeProvider>
		</ColorModeContext.Provider>
	);
}

export default App;