package com.cbthinkx.usa;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UsaTools extends JFrame implements ChangeListener {
	private static final long serialVersionUID = 1L;
	private JScrollPane jsp;
	private MyUsaPanel mup;
	double zoom = 1.0;
	public UsaTools() throws Exception {
		super("USA Tools");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		mup = new MyUsaPanel();
		mup.loadStates();
		jsp = new JScrollPane(mup);
		jsp.getViewport().setViewPosition(new Point(64, 64));
		add(jsp, BorderLayout.CENTER);
		JSlider js = configureZoom(new JSlider());
		js.addChangeListener(this);
		add(js, BorderLayout.SOUTH);
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	public static void main(String[] sa) throws Exception {
		new UsaTools();
	}
	public class MyUsaPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private List<Shape> shapes = new LinkedList<Shape>();
		public MyUsaPanel() {
			setLayout(null);
			setBackground(Color.WHITE);
		}
		@Override
		public Dimension getPreferredSize() {
			return new Dimension((int) (1024 * zoom), (int) (768 * zoom));
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			AffineTransform gat = new AffineTransform();
			gat.translate(getWidth() / 2.0, getHeight() / 2.0);
			gat.scale(zoom, -zoom);
			g2d.transform(gat);
			AffineTransform at = new AffineTransform();
			at.scale(0.000187, 0.000187);
			at.translate(179.231086, 14.601813);
			g2d.setStroke(new BasicStroke(0.0f));
			g2d.setPaint(Color.BLACK);
			for (Shape s : this.shapes) {
				g2d.draw(at.createTransformedShape(s));
			}
			g2d.dispose();
		}
		public void loadStates() throws Exception {
			BufferedReader br = new BufferedReader(
				new FileReader("usa2013.txt")
			);
			String s = "";
			while ((s = br.readLine()) != null) {
				Path2D p2d = new Path2D.Double();
				String[] points = s.trim().split(" ");
				for (int i = 0; i < points.length; i++) {
					String[] xy = points[i].split(",");
					Point2D pt2d = doAlbers(Double.parseDouble(xy[1]), Double.parseDouble(xy[0]));
					switch (i) {
						case 0:
							p2d.moveTo(pt2d.getX(), pt2d.getY());
							break;
						default:
							p2d.lineTo(pt2d.getX(), pt2d.getY());
							break;
					}
				}
				p2d.closePath();
				this.shapes.add(p2d);
			}
			br.close();
		}
		public Point2D doAlbers(double lat, double lon) { // doAlbers
			double latitude = Math.toRadians(lat);
			double longitude = Math.toRadians(lon);
			double phi0 = Math.toRadians(39.828127);
			double phi1 = Math.toRadians(28.25); //(29.5);
			double phi2 = Math.toRadians(45.25); //(45.5);
			double n = 0.5 * (Math.sin(phi1) + Math.sin(phi2));
			double lambda = longitude;
			double phi = latitude;
			double lambda0 = Math.toRadians(-99.0);
			double theta = n * (lambda - lambda0);
			double C = Math.cos(phi1) * Math.cos(phi1) + 2.0 * n * Math.sin(phi1);
			double rho = (Math.sqrt(C - 2.0 * n * Math.sin(phi))) / n;
			double rho0 = (Math.sqrt(C - 2.0 * n * Math.sin(phi0))) / n;
			double E = rho * Math.sin(theta);
			double N = rho0 - rho * Math.cos(theta);
			Point2D p2dd = new Point2D.Double(E * 6378296.400, N * 6378296.400);
			return p2dd;
		}
		@SuppressWarnings("unused")
		public Point2D doLambert(double lat, double lon) {
			double a = 6378296.400; // meters
			double f1 = 294.97870;
			double e = 0.08227185;
			double e2 = 0.00676866;
			double centerlon = 98.579404; //96.0;
			double sp1lat = 29.5; //33.0;
			double sp2lat = 45.5; //45.0;
			double folat = 39.828127; // 39.0;
			double phi1 = Math.toRadians(sp1lat);
			double phi2 = Math.toRadians(sp2lat);
			double phif = Math.toRadians(folat);
			double lambdao = -Math.toRadians(99.0);
			double Ef = 0.0;
			double Nf = 0.0;
			double phi = Math.toRadians(lat);
			double lambda = Math.toRadians(lon);
			double m1 = Math.cos(phi1) / Math.sqrt(1.0 - e * e * Math.sin(phi1) * Math.sin(phi1));
			double m2 = Math.cos(phi2) / Math.sqrt(1.0 - e * e * Math.sin(phi2) * Math.sin(phi2));
			double t = Math.tan(Math.PI / 4.0 - phi / 2.0) / Math.pow((1.0 - e * Math.sin(phi)) / (1.0 + e * Math.sin(phi)), e / 2.0);
			double t1 = Math.tan(Math.PI / 4.0 - phi1 / 2.0) / Math.pow((1.0 - e * Math.sin(phi1)) / (1.0 + e * Math.sin(phi1)), e / 2.0);
			double t2 = Math.tan(Math.PI / 4.0 - phi2 / 2.0) / Math.pow((1.0 - e * Math.sin(phi2)) / (1.0 + e * Math.sin(phi2)), e / 2.0);
			double tf = Math.tan(Math.PI / 4.0 - phif / 2.0) / Math.pow((1.0 - e * Math.sin(phif)) / (1.0 + e * Math.sin(phif)), e / 2.0);
			double n = (Math.log(m1) - Math.log(m2)) / (Math.log(t1) - Math.log(t2));
			double theta = n * (lambda - lambdao);
			double F = m1 / (n * Math.pow(t1, n));
			double r = a * F * Math.pow(t, n);
			double rf = a * F * Math.pow(tf, n);
			double E = Ef + r * Math.sin(theta);
			double N = Nf + rf - r * Math.cos(theta);
			Point2D p2dd = new Point2D.Double(E, N);
			return p2dd;
		}
	}
	private JSlider configureZoom(JSlider js) {
		js.setMaximum(12);
		js.setMinimum(0);
		js.setValue(4);
		js.setMajorTickSpacing(2);
		js.setMinorTickSpacing(1);
		js.setSnapToTicks(true);
		js.setPaintTicks(true);
		Hashtable<Integer, JComponent> d = new Hashtable<Integer, JComponent>();
		d.put(0, new JLabel("0.25"));
		d.put(2, new JLabel("0.5"));
		d.put(4, new JLabel("1.0"));
		d.put(6, new JLabel("2.0"));
		d.put(8, new JLabel("3.0"));
		d.put(10, new JLabel("4.0"));
		d.put(12, new JLabel( "5.0"));
		js.setLabelTable(d);
		js.getModel().setValueIsAdjusting(false);
		js.setPaintLabels(true);
		return js;
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		double oldZoom = zoom;
		if (e.getSource() instanceof JSlider) {
			JSlider js = (JSlider) e.getSource();
			if (!js.getModel().getValueIsAdjusting()) {
				switch (js.getValue()) {
					case 0:
						zoom = 0.25;
						break;
					case 1:
						zoom = 0.375;
						break;
					case 2:
						zoom = 0.5;
						break;
					case 3:
						zoom = 0.75;
						break;
					case 4:
						zoom = 1.0;
						break;
					case 5:
						zoom = 1.5;
						break;
					case 6:
						zoom = 2.0;
						break;
					case 7:
						zoom = 2.5;
						break;
					case 8:
						zoom = 3.0;
						break;
					case 9:
						zoom = 3.5;
						break;
					case 10:
						zoom = 4.0;
						break;
					case 11:
						zoom = 4.5;
						break;
					case 12:
						zoom = 5.0;
						break;
					default:
						break;
				}
				mup.setPreferredSize(new Dimension((int) (1024.0 * zoom), (int) (768.0 * zoom)));
				JViewport jvp = jsp.getViewport();
				Rectangle vpRect = jvp.getViewRect();
				double vpCenterX = vpRect.getWidth() / 2.0;
				double vpCenterY = vpRect.getHeight() / 2.0;
				double usaCenterX = vpCenterX + vpRect.getX();
				double usaCenterY = vpCenterY + vpRect.getY();
				double usaZoomCenterX = usaCenterX * (zoom / oldZoom);
				double usaZoomCenterY = usaCenterY * (zoom / oldZoom);
				double vpZoomX = usaZoomCenterX - vpRect.getWidth() / 2.0;
				double vpZoomY = usaZoomCenterY - vpRect.getHeight() / 2.0;
				jvp.setViewPosition(new Point(((int) vpZoomX),((int) vpZoomY)));
				mup.revalidate();
				jsp.repaint();
			}
		}
	}
}
