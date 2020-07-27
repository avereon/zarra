package com.avereon.venza.test;

import javafx.geometry.Point3D;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Description;

public class PointCloseTo extends TypeSafeMatcher<Point3D> {

	private final Point3D expected;

	private final double delta;

	public PointCloseTo( Point3D expected, double error ) {
		this.expected = expected;
		this.delta = error;
	}

	@Override
	protected boolean matchesSafely( Point3D item ) {
		return this.actualDelta( item ) <= 0.0D;
	}

	public void describeMismatchSafely( Point3D item, Description mismatchDescription ) {
		mismatchDescription
			.appendValue( item )
			.appendText( " differed by " )
			.appendValue( this.actualDelta( item ) )
			.appendText( " more than delta " )
			.appendValue( this.delta );
	}

	@Override
	public void describeTo( Description description ) {
		description.appendText( "a numeric value within " ).appendValue( this.delta ).appendText( " of " ).appendValue( this.expected );
	}

	private double actualDelta( Point3D item ) {
		return item.distance( expected ) - this.delta;
	}

	public static Matcher<Point3D> closeTo( Point3D operand, double error ) {
		return new PointCloseTo( operand, error );
	}

}
